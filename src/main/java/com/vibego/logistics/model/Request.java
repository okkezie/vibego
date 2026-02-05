package com.vibego.logistics.model;

import jakarta.persistence.*;
import com.vibego.logistics.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String packageType; // e.g., "electronics", "documents"
    private Double weight;
    private Boolean fragility;
    private String description;
    private String urgency; // "low", "medium", "high"
    private String category;
    private String pickupLocation;
    private String deliveryLocation;

    @Column(name = "order_id")
    private Long orderId;

    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;

    private Integer driversNotified = 0;
    private Integer driversAccepted = 0;
    private Integer driversFound = 0;

    private String callbackUrl;

    @ElementCollection
    private Set<Long> notifiedDrivers = new HashSet<>();

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
