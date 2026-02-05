package com.vibego.logistics.dto;

import com.vibego.logistics.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private Long id;

    @NotNull
    @Positive
    private Long requestId;

    @NotNull
    @Positive
    private Long vehicleId;

    @NotNull
    @Positive
    private Long driverId;

    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
