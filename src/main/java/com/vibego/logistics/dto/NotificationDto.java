package com.vibego.logistics.dto;

import com.vibego.logistics.enums.NotificationStatus;
import jakarta.validation.constraints.NotBlank;
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
public class NotificationDto {
    private Long id;

    @NotNull
    @Positive
    private Long driverId;

    @NotNull
    @Positive
    private Long requestId;

    @NotBlank
    private String message;

    private String type;
    private NotificationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
