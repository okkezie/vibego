package com.vibego.logistics.event;

import com.vibego.logistics.model.Driver;
import com.vibego.logistics.model.Request;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class NotificationEvent extends ApplicationEvent {
    private final Request request;
    private final List<Driver> drivers;

    public NotificationEvent(Object source, Request request, List<Driver> drivers) {
        super(source);
        this.request = request;
        this.drivers = drivers;
    }
}
