package com.vibego.logistics.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RequestEvent extends ApplicationEvent {
    private final Long requestId;

    public RequestEvent(Object source, Long requestId) {
        super(source);
        this.requestId = requestId;
    }
}
