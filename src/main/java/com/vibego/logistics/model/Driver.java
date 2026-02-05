package com.vibego.logistics.model;

import jakarta.persistence.*;
import com.vibego.logistics.enums.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "drivers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String gender;
    private LocalDate dob;
    private String address;
    private String addressCity;
    private String addressState;
    private String identityType;
    @Column(unique = true, nullable = false)
    private String identityNumber;
    private Boolean identityVerified = false;
    private Boolean addressVerified = false;
    private String educationLevel;
    private Boolean educationVerified = false;
    private String phone;
    @Column(unique = true)
    private String email;
    private Long userId; // link to User table

    @Enumerated(EnumType.STRING)
    private DriverStatus status = DriverStatus.AVAILABLE;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
