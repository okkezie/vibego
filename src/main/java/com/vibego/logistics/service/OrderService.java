package com.vibego.logistics.service;

import com.vibego.logistics.dto.OrderDto;
import com.vibego.logistics.enums.OrderStatus;
import com.vibego.logistics.enums.RequestStatus;
import com.vibego.logistics.model.Order;
import com.vibego.logistics.model.Request;
import com.vibego.logistics.repository.OrderRepository;
import com.vibego.logistics.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        Order order = mapToEntity(orderDto);
        Order saved = orderRepository.save(order);
        // Update request if needed
        updateRequestWithOrder(saved);
        return mapToDto(saved);
    }

    @Transactional
    public OrderDto cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);

        // Update associated request
        Optional<Request> requestOpt = requestRepository.findById(order.getRequestId());
        if (requestOpt.isPresent()) {
            Request request = requestOpt.get();
            request.setStatus(RequestStatus.CANCELLED);
            request.setOrderId(null); // or keep for history
            requestRepository.save(request);
        }
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
        List<Order> orders = orderRepository.findByDriverIdAndStatus(driverId, OrderStatus.ACCEPTED);
        return orders.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return mapToDto(order);
    }
}
