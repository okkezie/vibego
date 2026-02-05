package com.vibego.logistics.controller;

import com.vibego.logistics.dto.RequestDto;
import com.vibego.logistics.enums.RequestStatus;
import com.vibego.logistics.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<EntityModel<RequestDto>> createRequest(@Valid @RequestBody RequestDto requestDto) {
        log.info("Received request to create new delivery request for user: {}", requestDto.getUserId());
        RequestDto created = requestService.createRequest(requestDto);
        EntityModel<RequestDto> resource = EntityModel.of(created);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RequestController.class).getRequestStatus(created.getId())).withSelfRel();
        Link statusLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RequestController.class).getRequestStatus(created.getId())).withRel("status");
        resource.add(selfLink, statusLink);
        URI location = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RequestController.class).getRequestStatus(created.getId())).toUri();
        log.info("Successfully created request ID: {} with status: {}", created.getId(), created.getStatus());
        return ResponseEntity.accepted().location(location).body(resource);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<EntityModel<RequestDto>> getRequestStatus(@PathVariable Long id) {
        log.info("Received request for status of request ID: {}", id);
        RequestDto dto = requestService.getRequestStatus(id);
        EntityModel<RequestDto> resource = EntityModel.of(dto);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RequestController.class).getRequestStatus(id)).withSelfRel();
        resource.add(selfLink);
        // Add other links e.g. to order if exists
        if (dto.getOrderId() != null) {
            Link orderLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).getOrder(dto.getOrderId())).withRel("order");
            resource.add(orderLink);
        }
        log.info("Returning status for request ID: {} (status: {})", id, dto.getStatus());
        return ResponseEntity.ok(resource);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<EntityModel<RequestDto>> updateStatus(@PathVariable Long id, @RequestParam RequestStatus status) {
        log.info("Received status update for request ID: {} to {}", id, status);
        RequestDto updated = requestService.updateRequestStatus(id, status);
        EntityModel<RequestDto> resource = EntityModel.of(updated);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RequestController.class).getRequestStatus(id)).withSelfRel();
        resource.add(selfLink);
        log.info("Updated and returning status for request ID: {}", id);
        return ResponseEntity.ok(resource);
    }

    // Other endpoints following REST: GET /api/requests , etc.
    @GetMapping
    public ResponseEntity<List<EntityModel<RequestDto>>> getAllRequests() {
        // Implement as needed, for demo return empty or add
        log.info("Received request for all requests (placeholder implementation)");
        List<EntityModel<RequestDto>> resources = List.of(); // placeholder
        return ResponseEntity.ok(resources);
    }
}
