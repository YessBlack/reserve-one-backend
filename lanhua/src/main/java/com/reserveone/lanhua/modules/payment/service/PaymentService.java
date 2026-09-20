package com.reserveone.lanhua.modules.payment.service;

import com.reserveone.lanhua.modules.membership.entity.Membership;
import com.reserveone.lanhua.modules.membership.repository.MembershipRepository;
import com.reserveone.lanhua.modules.payment.client.BoldClient;
import com.reserveone.lanhua.modules.payment.client.BoldLink;
import com.reserveone.lanhua.modules.payment.dto.PaymentRequestDTO;
import com.reserveone.lanhua.modules.payment.dto.PaymentResponseDTO;
import com.reserveone.lanhua.modules.payment.entity.Payment;
import com.reserveone.lanhua.modules.payment.entity.PaymentStatus;
import com.reserveone.lanhua.modules.payment.repository.PaymentRepository;
import com.reserveone.lanhua.modules.user.entity.User;
import com.reserveone.lanhua.modules.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final BoldClient boldClient;

    public PaymentService(PaymentRepository paymentRepository,
                          UserRepository userRepository,
                          MembershipRepository membershipRepository,
                          BoldClient boldClient) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
        this.boldClient = boldClient;
    }

    public PaymentResponseDTO createPayment(String userEmail, PaymentRequestDTO dto) {
        User user = findUserByEmail(userEmail);

        Membership membership = membershipRepository
                .findById(dto.getIdMembership())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan no encontrado"));

        long amount = membership.getPrice().setScale(0, RoundingMode.HALF_UP).longValueExact();
        String description = "Mensualidad " + membership.getName();
        String reference = "LANHUA-" + UUID.randomUUID();

        BoldLink link;
        try {
            link = boldClient.createLink(reference, amount, description);
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "No se pudo crear el link de pago en Bold: " + e.getMessage());
        }

        Payment payment = Payment.builder()
                .reference(reference)
                .boldLinkId(link.id())
                .checkoutUrl(link.url())
                .amount(amount)
                .currency("COP")
                .description(description)
                .status(PaymentStatus.PENDING)
                .idUser(user.getIdUser())
                .idMembership(membership.getId())
                .build();

        return mapToResponseDTO(paymentRepository.save(payment));
    }

    @Transactional
    public PaymentResponseDTO refreshStatus(String userEmail, String reference) {
        User user = findUserByEmail(userEmail);

        Payment payment = paymentRepository.findByReference(reference)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pago no encontrado"));

        // 404 y no 403, para no revelar que ese pago existe
        if (!payment.getIdUser().equals(user.getIdUser())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pago no encontrado");
        }

        try {
            String boldStatus = boldClient.getLinkStatus(payment.getBoldLinkId());
            payment.setStatus(mapStatus(boldStatus));
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "No se pudo consultar el estado en Bold: " + e.getMessage());
        }

        if (payment.getStatus() == PaymentStatus.PAID && !payment.isMembershipActivated()) {
            activateMembership(payment, user);
        }

        return mapToResponseDTO(paymentRepository.save(payment));
    }

    private void activateMembership(Payment payment, User user) {
        Membership membership = membershipRepository.findById(payment.getIdMembership())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan no encontrado"));

        // Si el plan sigue vigente, se suman 30 días al vencimiento; si no, desde hoy
        LocalDate base = user.hasActiveMembership() ? user.getMembershipEndDate() : LocalDate.now();

        user.setMembership(membership);
        user.setMembershipEndDate(base.plusDays(30));
        userRepository.save(user);

        payment.setMembershipActivated(true);
    }

    private User findUserByEmail(String userEmail) {
        return userRepository.findByEmailUser(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
    }

    private PaymentStatus mapStatus(String boldStatus) {
        if (boldStatus == null) return PaymentStatus.PENDING;
        return switch (boldStatus) {
            case "PROCESSING" -> PaymentStatus.PROCESSING;
            case "PAID" -> PaymentStatus.PAID;
            case "REJECTED" -> PaymentStatus.REJECTED;
            case "CANCELLED" -> PaymentStatus.CANCELLED;
            case "EXPIRED" -> PaymentStatus.EXPIRED;
            default -> PaymentStatus.PENDING; // ACTIVE
        };
    }

    private PaymentResponseDTO mapToResponseDTO(Payment payment) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setIdPayment(payment.getIdPayment());
        dto.setReference(payment.getReference());
        dto.setCheckoutUrl(payment.getCheckoutUrl());
        dto.setAmount(payment.getAmount());
        dto.setCurrency(payment.getCurrency());
        dto.setDescription(payment.getDescription());
        dto.setStatus(payment.getStatus());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());
        return dto;
    }
}