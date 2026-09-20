package com.reserveone.lanhua.modules.payment.repository;

import com.reserveone.lanhua.modules.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByReference(String reference);
}
