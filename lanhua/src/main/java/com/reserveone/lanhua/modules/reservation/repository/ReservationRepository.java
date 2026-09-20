package com.reserveone.lanhua.modules.reservation.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.reserveone.lanhua.modules.reservation.entity.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUsers_IdUser(Long idUser);

    List<Reservation> findBySchedule_IdSchedule(Long idSchedule);

    @Query("""
            SELECT COUNT(r) > 0
            FROM Reservation r
            JOIN r.users u
            WHERE u.idUser = :idUser
              AND r.schedule.idSchedule = :idSchedule
              AND UPPER(r.reservationState) <> 'CANCELADA'
            """)
    boolean existsActiveReservationByUserAndSchedule(@Param("idUser") Long idUser,
                                                     @Param("idSchedule") Long idSchedule);

    @Query("""
            SELECT COUNT(r) > 0
            FROM Reservation r
            JOIN r.users u
            WHERE u.idUser = :idUser
              AND r.schedule.scheduleDate = :scheduleDate
              AND UPPER(r.reservationState) = 'CONFIRMED'
            """)
    boolean existsConfirmedReservationByUserAndScheduleDate(@Param("idUser") Long idUser,
                                                            @Param("scheduleDate") LocalDateTime scheduleDate);
}