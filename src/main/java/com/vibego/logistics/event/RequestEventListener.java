package com.vibego.logistics.event;

import com.vibego.logistics.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestEventListener {

    private final RequestService requestService;

    @Async
    @EventListener
    public void handleRequestEvent(RequestEvent event) {
        log.info("Received request created event for ID: {}", event.getRequestId());
        requestService.processRequest(event.getRequestId());
        log.info("Completed handling request event for ID: {}", event.getRequestId());
    }
}
