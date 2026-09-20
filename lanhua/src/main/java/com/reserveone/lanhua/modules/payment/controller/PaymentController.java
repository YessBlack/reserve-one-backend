package com.reserveone.lanhua.modules.payment.controller;

import com.reserveone.lanhua.modules.payment.dto.PaymentRequestDTO;
import com.reserveone.lanhua.modules.payment.dto.PaymentResponseDTO;
import com.reserveone.lanhua.modules.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> create(Authentication auth,
                                                     @Valid @RequestBody PaymentRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.createPayment(auth.getName(), dto));
    }

    @GetMapping("/{reference}/status")
    public PaymentResponseDTO status(Authentication auth, @PathVariable String reference) {
        return paymentService.refreshStatus(auth.getName(), reference);
    }
}