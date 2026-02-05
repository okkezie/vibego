package com.vibego.logistics.service;

import com.vibego.logistics.dto.NotificationDto;
import com.vibego.logistics.enums.DriverStatus;
import com.vibego.logistics.enums.NotificationStatus;
import com.vibego.logistics.enums.OrderStatus;
import com.vibego.logistics.enums.RequestStatus;
import com.vibego.logistics.event.NotificationResponseEvent;
import com.vibego.logistics.model.*;
import com.vibego.logistics.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final RequestRepository requestRepository;

    private final OrderRepository orderRepository;

    private final DriverRepository driverRepository;

    private final VehicleDriverRepository vehicleDriverRepository;

    private final ApplicationEventPublisher eventPublisher;

    public List<NotificationDto> getPendingNotifications(Long driverId) {
        log.info("Fetching pending notifications for driver: {}", driverId);
        List<Notification> notifications = notificationRepository.findByDriverIdAndStatus(driverId, NotificationStatus.PENDING);
        log.info("Found {} pending notifications for driver {}", notifications.size(), driverId);
        return notifications.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public NotificationDto respondToNotification(Long notificationId, boolean accept, Long driverId) {
        log.info("Responding to notification {} from driver {} (accept: {})", notificationId, driverId, accept);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getDriverId().equals(driverId)) {
            log.error("Unauthorized access attempt by driver {} to notification {}", driverId, notificationId);
            throw new RuntimeException("Unauthorized");
        }

        if (accept) {
            notification.setStatus(NotificationStatus.ACCEPTED);
        } else {
            notification.setStatus(NotificationStatus.DECLINED);
        }
        Notification saved = notificationRepository.save(notification);

        eventPublisher.publishEvent(new NotificationResponseEvent(this, notificationId, accept, driverId, notification.getRequestId()));
        log.info("Published response event for notification {} (accept: {})", notificationId, accept);
        return mapToDto(saved);
    }

    private void createOrderForRequest(Long requestId, Long driverId) {
        // Find available vehicle for driver
        List<VehicleDriver> assignments = vehicleDriverRepository.findByDriverIdAndStatus(driverId, DriverStatus.AVAILABLE);
        if (!assignments.isEmpty()) {
            Long vehicleId = assignments.get(0).getVehicleId();
            Order order = new Order();
            order.setRequestId(requestId);
            order.setDriverId(driverId);
            order.setVehicleId(vehicleId);
            order.setStatus(OrderStatus.ACCEPTED);
            Order savedOrder = orderRepository.save(order);
            log.info("Created order {} for request {} and driver {}", savedOrder.getId(), requestId, driverId);

            // Update request
            Request request = requestRepository.findById(requestId).orElseThrow();
            request.setOrderId(savedOrder.getId());
            request.setStatus(RequestStatus.ORDER_CREATED);
            request.setDriversAccepted(request.getDriversAccepted() + 1);
            requestRepository.save(request);
            log.info("Updated request {} with order and status ORDER_CREATED", requestId);
        } else {
            log.warn("No available vehicle found for driver {} on request {}", driverId, requestId);
        }
    }

    private void updateRequestAfterAccept(Long requestId, Long driverId) {
        // Could mark driver busy etc.
        Driver driver = driverRepository.findById(driverId).orElseThrow();
        driver.setStatus(DriverStatus.BUSY);
        driverRepository.save(driver);
        log.info("Updated driver {} status to BUSY after accepting request {}", driverId, requestId);
    }

    private NotificationDto mapToDto(Notification entity) {
        NotificationDto dto = new NotificationDto();
        dto.setId(entity.getId());
        dto.setDriverId(entity.getDriverId());
        dto.setRequestId(entity.getRequestId());
        dto.setMessage(entity.getMessage());
        dto.setType(entity.getType());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setExpiresAt(entity.getExpiresAt());
        return dto;
    }

    @Async
    public void notifyDrivers(Request request, List<Driver> drivers) {
        log.info("Notifying {} drivers for request {}", drivers.size(), request.getId());
        for (Driver driver : drivers) {
            Notification notification = new Notification();
            notification.setDriverId(driver.getId());
            notification.setRequestId(request.getId());
            notification.setMessage("New delivery request: " + request.getDescription() + ". Weight: " + request.getWeight() + "kg. Accept?");
            notification.setType("NEW_REQUEST");
            notification.setExpiresAt(LocalDateTime.now().plusMinutes(30));
            notificationRepository.save(notification);
            request.getNotifiedDrivers().add(driver.getId());
            log.debug("Sent notification to driver {} for request {}", driver.getId(), request.getId());
        }
        log.info("Completed notifying drivers for request {}", request.getId());
    }

    @Async
    @Transactional
    public void processNotificationResponse(Long notificationId, boolean accept, Long driverId, Long requestId) {
        log.info("Processing async notification response for ID {} (accept: {}, driver: {}, request: {})", notificationId, accept, driverId, requestId);
        if (accept) {
            createOrderForRequest(requestId, driverId);
            updateRequestAfterAccept(requestId, driverId);
        } else {
            log.info("Declined notification {}, no order created", notificationId);
        }
        log.info("Completed async processing for notification response {}", notificationId);
    }
}
