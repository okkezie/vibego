package com.vibego.logistics.event;

import com.vibego.logistics.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCancellationEventListener {

    private final OrderService orderService;

    @Async
    @EventListener
    public void handleCancellationEvent(OrderCancellationEvent event) {
        log.info("Received order cancellation event for ID: {}", event.getOrderId());
        orderService.processOrderCancellation(event.getOrderId());
        log.info("Completed handling cancellation event for order ID: {}", event.getOrderId());
    }
}
