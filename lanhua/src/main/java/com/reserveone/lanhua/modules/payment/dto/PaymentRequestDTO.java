package com.reserveone.lanhua.modules.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequestDTO {

    @NotNull(message = "El plan es obligatorio")
    private Long idMembership;
}