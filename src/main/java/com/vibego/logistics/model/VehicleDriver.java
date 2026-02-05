package com.vibego.logistics.model;

import jakarta.persistence.*;
import com.vibego.logistics.enums.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_drivers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDriver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long vehicleId;
    private Long driverId;

    @Enumerated(EnumType.STRING)
    private DriverStatus status = DriverStatus.AVAILABLE; // or specific association status

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
