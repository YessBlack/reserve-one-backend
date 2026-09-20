package com.reserveone.lanhua.modules.payment.dto;

import com.reserveone.lanhua.modules.payment.entity.PaymentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentResponseDTO {
    private Integer idPayment;
    private String reference;
    private String checkoutUrl;
    private Long amount;
    private String currency;
    private String description;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}