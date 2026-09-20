package com.reserveone.lanhua.modules.reservation.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.reserveone.lanhua.modules.catalog.dto.CatalogSummaryDTO;
import com.reserveone.lanhua.modules.class_schedule.dto.ScheduleSummaryDTO;
import com.reserveone.lanhua.modules.user.dto.UserSummaryDTO;
import org.springframework.stereotype.Service;

import com.reserveone.lanhua.modules.reservation.dto.ReservationRequestDto;
import com.reserveone.lanhua.modules.reservation.dto.ReservationResponseDto;
import com.reserveone.lanhua.modules.reservation.entity.Reservation;
import com.reserveone.lanhua.modules.reservation.repository.ReservationRepository;
import com.reserveone.lanhua.modules.schedule.entity.Schedule;
import com.reserveone.lanhua.modules.schedule.repository.ScheduleRepository;
import com.reserveone.lanhua.modules.user.entity.User;
import com.reserveone.lanhua.modules.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReservationService {

    private static final String STATE_PENDING = "PENDIENTE";
    private static final String STATE_CANCELLED = "CANCELADA";

    private final ReservationRepository reservationRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              ScheduleRepository scheduleRepository,
                              UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
    }

    public List<ReservationResponseDto> listReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ReservationResponseDto> listByUser(Long idUser) {
        return reservationRepository.findByUsers_IdUser(idUser)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ReservationResponseDto getReservationById(Long id) {
        return mapToResponse(findReservationOrThrow(id));
    }

    @Transactional
    public ReservationResponseDto createReservation(ReservationRequestDto dto) {
        List<User> users = userRepository.findAllById(dto.getIdUsers());
        if (users.isEmpty()) {
            throw new RuntimeException("No se encontraron usuarios válidos para la reserva");
        }

        Schedule schedule = scheduleRepository.findById(dto.getIdSchedule())
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        if (schedule.getQuotas() == null || schedule.getQuotas() < users.size()) {
            throw new RuntimeException("No hay suficientes cupos disponibles para este horario");
        }

        validateUsersCanReserve(users, schedule);

        schedule.setQuotas(schedule.getQuotas() - users.size());
        scheduleRepository.save(schedule);

        Reservation reservation = new Reservation();
        reservation.setUsers(users);
        reservation.setSchedule(schedule);
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setReservationState(STATE_PENDING);

        Reservation saved = reservationRepository.save(reservation);
        return mapToResponse(saved);
    }

    private void validateUsersCanReserve(List<User> users, Schedule schedule) {
        for (User user : users) {
            Long userId = user.getIdUser();

            if (reservationRepository.existsActiveReservationByUserAndSchedule(userId, schedule.getIdSchedule())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "El usuario ya tiene una reserva activa para esta clase"
                );
            }

            if (reservationRepository.existsConfirmedReservationByUserAndScheduleDate(userId, schedule.getScheduleDate())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "El usuario ya tiene una reserva confirmada en este horario"
                );
            }
        }
    }

    public ReservationResponseDto cancelReservation(Long id) {
        Reservation reservation = findReservationOrThrow(id);

        if (STATE_CANCELLED.equals(reservation.getReservationState())) {
            throw new RuntimeException("La reserva ya estaba cancelada");
        }

        reservation.setReservationState(STATE_CANCELLED);

        Schedule schedule = reservation.getSchedule();
        schedule.setQuotas(schedule.getQuotas() + reservation.getUsers().size());
        scheduleRepository.save(schedule);

        Reservation updated = reservationRepository.save(reservation);
        return mapToResponse(updated);
    }

    private Reservation findReservationOrThrow(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
    }


    private ReservationResponseDto mapToResponse(Reservation reservation) {
        Long userId = reservation.getUsers().stream()
                .findFirst()
                .map(User::getIdUser)
                .orElse(null);

        Schedule scheduleEntity = reservation.getSchedule();

        ScheduleSummaryDTO schedule = new ScheduleSummaryDTO(
                scheduleEntity.getIdSchedule(),
                scheduleEntity.getQuotas(),
                scheduleEntity.getScheduleDate(),
                scheduleEntity.getLocation(),
                scheduleEntity.getImage()
        );

        CatalogSummaryDTO catalog = new CatalogSummaryDTO(
                scheduleEntity.getCatalog().getIdCatalog(),
                scheduleEntity.getCatalog().getName(),
                scheduleEntity.getCatalog().getImage()
        );

        return new ReservationResponseDto(
                reservation.getIdReservation(),
                userId,
                schedule,
                catalog,
                scheduleEntity.getModality(),
                reservation.getReservationState(),
                reservation.getReservationDate(),
                reservation.getCreatedAt()
        );
    }

    public void confirmUserReservations(Long idUser) {
        List<Reservation> userReservations = reservationRepository.findByUsers_IdUser(idUser);

        for (Reservation res : userReservations) {
            if ("PENDIENTE".equalsIgnoreCase(res.getReservationState()) || "PENDING".equalsIgnoreCase(res.getReservationState())) {
                res.setReservationState("CONFIRMED");
                reservationRepository.save(res);
            }
        }
    }
}