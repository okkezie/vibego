package com.vibego.logistics.event;

import com.vibego.logistics.model.Driver;
import com.vibego.logistics.model.Request;
import com.vibego.logistics.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationService notificationService;

    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        log.info("Received notification event for request ID: {}", event.getRequest().getId());
        notificationService.notifyDrivers(event.getRequest(), event.getDrivers());
        log.info("Completed handling notification event for request ID: {}", event.getRequest().getId());
    }
}
