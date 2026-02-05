package com.vibego.logistics.service;

import com.vibego.logistics.dto.OrderDto;
import com.vibego.logistics.enums.OrderStatus;
import com.vibego.logistics.enums.RequestStatus;
import com.vibego.logistics.event.OrderCancellationEvent;
import com.vibego.logistics.model.Order;
import com.vibego.logistics.model.Request;
import com.vibego.logistics.repository.OrderRepository;
import com.vibego.logistics.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    private final RequestRepository requestRepository;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        log.info("Creating order for request ID: {}", orderDto.getRequestId());
        Order order = mapToEntity(orderDto);
        Order saved = orderRepository.save(order);
        // Update request if needed
        updateRequestWithOrder(saved);
        log.info("Created order ID: {} with status: {}", saved.getId(), saved.getStatus());
        return mapToDto(saved);
    }

    @Transactional
    public OrderDto cancelOrder(Long orderId) {
        log.info("Received cancel request for order ID: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);

        eventPublisher.publishEvent(new OrderCancellationEvent(this, orderId));
        log.info("Published cancellation event for order ID: {}", orderId);
        return mapToDto(saved);
    }

    private void updateRequestWithOrder(Order order) {
        // Already handled in notification mostly
    }

    private Order mapToEntity(OrderDto dto) {
        Order entity = new Order();
        entity.setRequestId(dto.getRequestId());
        entity.setVehicleId(dto.getVehicleId());
        entity.setDriverId(dto.getDriverId());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : OrderStatus.PENDING);
        return entity;
    }

    private OrderDto mapToDto(Order entity) {
        OrderDto dto = new OrderDto();
        dto.setId(entity.getId());
        dto.setRequestId(entity.getRequestId());
        dto.setVehicleId(entity.getVehicleId());
        dto.setDriverId(entity.getDriverId());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public List<OrderDto> getOrdersByDriver(Long driverId) {
        log.info("Fetching orders for driver: {}", driverId);
        List<Order> orders = orderRepository.findByDriverIdAndStatus(driverId, OrderStatus.ACCEPTED);
        log.info("Found {} orders for driver: {}", orders.size(), driverId);
        return orders.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public OrderDto getOrderById(Long id) {
        log.info("Fetching order details for ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        log.info("Returning details for order ID: {}", id);
        return mapToDto(order);
    }

    @Async
    @Transactional
    public void processOrderCancellation(Long orderId) {
        log.info("Processing async cancellation for order ID: {}", orderId);
        // Load order to get associated requestId
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found for cancellation"));
        Long requestId = order.getRequestId();
        // Update associated request
        Optional<Request> requestOpt = requestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            Request request = requestOpt.get();
            request.setStatus(RequestStatus.CANCELLED);
            request.setOrderId(null); // or keep for history
            requestRepository.save(request);
            log.info("Updated request status to CANCELLED for cancelled order {}", orderId);
        } else {
            log.warn("No associated request found for cancelled order {}", orderId);
        }
        log.info("Completed async cancellation processing for order ID: {}", orderId);
    }
}
