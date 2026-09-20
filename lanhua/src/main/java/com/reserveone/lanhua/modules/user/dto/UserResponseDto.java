package com.reserveone.lanhua.modules.user.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserResponseDto {
    private Long idUser;
    private String nameUser;
    private String lastNameUser;
    private String emailUser;
    private String nameRol;
    private String membershipName;
    private LocalDate membershipEndDate;
    private boolean hasActiveMembership;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
}