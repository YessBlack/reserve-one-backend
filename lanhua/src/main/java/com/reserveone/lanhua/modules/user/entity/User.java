package com.reserveone.lanhua.modules.user.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.reserveone.lanhua.modules.membership.entity.Membership;
import jakarta.persistence.*;

import lombok.Data;
import com.reserveone.lanhua.modules.user_information.entity.UserInformation;

@Entity 
@Table(name = "users")
@Data 

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long idUser;

    @Column(name = "name_user", nullable = false, length = 40)
    private String nameUser;

    @Column(name = "last_name_user", nullable = false, length = 40)
    private String lastNameUser;

    @Column(name = "email_user", unique = true, nullable = false, length = 100)
    private String emailUser;

    @Column(name = "password_user", nullable = false, length = 255)
    private String passwordUser;

    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserInformation userInformation;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
        updateDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateDate = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_membership")
    private Membership membership;

    @Column(name = "membership_end_date")
    private LocalDate membershipEndDate;

    public boolean hasActiveMembership() {
        return membership != null
                && membershipEndDate != null
                && !membershipEndDate.isBefore(LocalDate.now());
    }
}
