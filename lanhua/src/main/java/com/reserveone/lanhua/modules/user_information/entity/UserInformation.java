package com.reserveone.lanhua.modules.user_information.entity;

import com.reserveone.lanhua.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user_information")
public class UserInformation {

    // Getters y Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user_information")
    private Integer idUserInformation;

    // Relación relacional real
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", referencedColumnName = "id_user")
    private User user;

    @Column(name = "number_dni", length = 20)
    private String numberDni;

    @Column(name = "address", length = 100)
    private String address;

    @Column(name = "user_phone", length = 20)
    private String userPhone;

    @Column(name = "contact_name", length = 50)
    private String contactName;

    @Column(name = "kinship", length = 20)
    private String kinship;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "EPS", length = 20)
    private String eps;

    @Column(name = "rh", length = 10)
    private String rh;

    @Column(name = "medic_conditions", columnDefinition = "TEXT")
    private String medicConditions;

    @Column(name = "document_url", columnDefinition = "TEXT")
    private String documentUrl;

    @Column(name = "eps_url", columnDefinition = "TEXT")
    private String epsUrl;

    @Column(name = "date_eps")
    private LocalDate dateEps;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public UserInformation() {}
}
