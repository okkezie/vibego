package com.vibego.logistics.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NotificationResponseEvent extends ApplicationEvent {
    private final Long notificationId;
    private final boolean accept;
    private final Long driverId;
    private final Long requestId;

    public NotificationResponseEvent(Object source, Long notificationId, boolean accept, Long driverId, Long requestId) {
        super(source);
        this.notificationId = notificationId;
        this.accept = accept;
        this.driverId = driverId;
        this.requestId = requestId;
    }
}
