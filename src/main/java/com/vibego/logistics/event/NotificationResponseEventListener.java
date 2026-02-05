package com.vibego.logistics.event;

import com.vibego.logistics.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationResponseEventListener {

    private final NotificationService notificationService;

    @Async
    @EventListener
    public void handleResponseEvent(NotificationResponseEvent event) {
        log.info("Received notification response event for ID: {} (accept: {})", event.getNotificationId(), event.isAccept());
        notificationService.processNotificationResponse(event.getNotificationId(), event.isAccept(), event.getDriverId(), event.getRequestId());
        log.info("Completed handling response event for notification ID: {}", event.getNotificationId());
    }
}
