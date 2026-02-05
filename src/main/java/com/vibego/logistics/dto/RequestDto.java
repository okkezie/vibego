package com.vibego.logistics.dto;

import com.vibego.logistics.enums.RequestStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestDto {
    private Long id;

    @NotBlank
    private String userId;

    @NotBlank
    private String packageType;

    @Positive
    private Double weight;

    private Boolean fragility;

    @Size(max = 500)
    private String description;

    @NotBlank
    private String urgency;

    @NotBlank
    private String category;

    @NotBlank
    private String pickupLocation;

    @NotBlank
    private String deliveryLocation;

    private Long orderId;
    private RequestStatus status;
    private Integer driversNotified;
    private Integer driversAccepted;
    private Integer driversFound;

    private String callbackUrl;

    private Set<Long> notifiedDrivers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
