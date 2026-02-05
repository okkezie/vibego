package com.vibego.logistics.repository;

import com.vibego.logistics.model.Notification;
import com.vibego.logistics.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByDriverIdAndStatus(Long driverId, NotificationStatus status);
    List<Notification> findByRequestId(Long requestId);
}
