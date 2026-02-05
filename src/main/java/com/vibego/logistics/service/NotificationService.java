package com.vibego.logistics.service;

import com.vibego.logistics.dto.NotificationDto;
import com.vibego.logistics.enums.DriverStatus;
import com.vibego.logistics.enums.NotificationStatus;
import com.vibego.logistics.enums.OrderStatus;
import com.vibego.logistics.enums.RequestStatus;
import com.vibego.logistics.model.*;
import com.vibego.logistics.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private VehicleDriverRepository vehicleDriverRepository;

    public List<NotificationDto> getPendingNotifications(Long driverId) {
        List<Notification> notifications = notificationRepository.findByDriverIdAndStatus(driverId, NotificationStatus.PENDING);
        return notifications.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public NotificationDto respondToNotification(Long notificationId, boolean accept, Long driverId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getDriverId().equals(driverId)) {
            throw new RuntimeException("Unauthorized");
        }

        if (accept) {
            notification.setStatus(NotificationStatus.ACCEPTED);
            // Create order
            createOrderForRequest(notification.getRequestId(), driverId);
            // Update request
            updateRequestAfterAccept(notification.getRequestId(), driverId);
        } else {
            notification.setStatus(NotificationStatus.DECLINED);
        }
        Notification saved = notificationRepository.save(notification);
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

            // Update request
            Request request = requestRepository.findById(requestId).orElseThrow();
            request.setOrderId(savedOrder.getId());
            request.setStatus(RequestStatus.ORDER_CREATED);
            request.setDriversAccepted(request.getDriversAccepted() + 1);
            requestRepository.save(request);
        }
    }

    private void updateRequestAfterAccept(Long requestId, Long driverId) {
        // Could mark driver busy etc.
        Driver driver = driverRepository.findById(driverId).orElseThrow();
        driver.setStatus(DriverStatus.BUSY);
        driverRepository.save(driver);
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
}
