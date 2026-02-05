package com.vibego.logistics.controller;

import com.vibego.logistics.dto.OrderDto;
import com.vibego.logistics.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<EntityModel<OrderDto>> createOrder(@Valid @RequestBody OrderDto orderDto) {
        log.info("Received request to create order for request ID: {}", orderDto.getRequestId());
        OrderDto created = orderService.createOrder(orderDto);
        EntityModel<OrderDto> resource = EntityModel.of(created);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).getOrder(created.getId())).withSelfRel();
        resource.add(selfLink);
        log.info("Successfully created order ID: {} with status: {}", created.getId(), created.getStatus());
        return ResponseEntity.created(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).getOrder(created.getId())).toUri()).body(resource);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<OrderDto>> getOrder(@PathVariable Long id) {
        log.info("Received request for order details ID: {}", id);
        OrderDto dto = orderService.getOrderById(id);
        EntityModel<OrderDto> resource = EntityModel.of(dto);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).getOrder(id)).withSelfRel();
        resource.add(selfLink);
        log.info("Returning details for order ID: {}", id);
        return ResponseEntity.ok(resource);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<EntityModel<OrderDto>> cancelOrder(@PathVariable Long id) {
        log.info("Received cancel request for order ID: {}", id);
        OrderDto cancelled = orderService.cancelOrder(id);
        EntityModel<OrderDto> resource = EntityModel.of(cancelled);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).getOrder(id)).withSelfRel();
        resource.add(selfLink);
        log.info("Cancelled order ID: {}", id);
        return ResponseEntity.ok(resource);
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<EntityModel<OrderDto>>> getDriverOrders(@PathVariable Long driverId) {
        log.info("Received request for orders of driver: {}", driverId);
        List<OrderDto> orders = orderService.getOrdersByDriver(driverId);
        // Add HATEOAS links similarly
        List<EntityModel<OrderDto>> resources = orders.stream().map(orderDto -> {
            EntityModel<OrderDto> em = EntityModel.of(orderDto);
            // add links
            return em;
        }).toList();
        log.info("Returning {} orders for driver: {}", resources.size(), driverId);
        return ResponseEntity.ok(resources);
    }
}
