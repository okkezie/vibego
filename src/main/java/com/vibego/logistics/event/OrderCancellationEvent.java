package com.vibego.logistics.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OrderCancellationEvent extends ApplicationEvent {
    private final Long orderId;

    public OrderCancellationEvent(Object source, Long orderId) {
        super(source);
        this.orderId = orderId;
    }
}
