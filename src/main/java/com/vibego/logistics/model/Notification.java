package com.vibego.logistics.model;

import jakarta.persistence.*;
import com.vibego.logistics.enums.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long driverId;
    private Long requestId;
    private String message;
    private String type; // e.g., "NEW_REQUEST"

    @Enumerated(EnumType.STRING)
    private NotificationStatus status = NotificationStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime expiresAt;
}
