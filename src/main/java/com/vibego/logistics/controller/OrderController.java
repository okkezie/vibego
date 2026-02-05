package com.vibego.logistics.controller;

import com.vibego.logistics.dto.OrderDto;
import com.vibego.logistics.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<EntityModel<OrderDto>> createOrder(@Valid @RequestBody OrderDto orderDto) {
        OrderDto created = orderService.createOrder(orderDto);
        EntityModel<OrderDto> resource = EntityModel.of(created);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).getOrder(created.getId())).withSelfRel();
        resource.add(selfLink);
        return ResponseEntity.created(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).getOrder(created.getId())).toUri()).body(resource);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<OrderDto>> getOrder(@PathVariable Long id) {
        OrderDto dto = orderService.getOrderById(id);
        EntityModel<OrderDto> resource = EntityModel.of(dto);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).getOrder(id)).withSelfRel();
        resource.add(selfLink);
        return ResponseEntity.ok(resource);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<EntityModel<OrderDto>> cancelOrder(@PathVariable Long id) {
        OrderDto cancelled = orderService.cancelOrder(id);
        EntityModel<OrderDto> resource = EntityModel.of(cancelled);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).getOrder(id)).withSelfRel();
        resource.add(selfLink);
        return ResponseEntity.ok(resource);
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<EntityModel<OrderDto>>> getDriverOrders(@PathVariable Long driverId) {
        List<OrderDto> orders = orderService.getOrdersByDriver(driverId);
        // Add HATEOAS links similarly
        List<EntityModel<OrderDto>> resources = orders.stream().map(orderDto -> {
            EntityModel<OrderDto> em = EntityModel.of(orderDto);
            // add links
            return em;
        }).toList();
        return ResponseEntity.ok(resources);
    }
}
