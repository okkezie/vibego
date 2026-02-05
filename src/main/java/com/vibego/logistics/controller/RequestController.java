package com.vibego.logistics.controller;

import com.vibego.logistics.dto.RequestDto;
import com.vibego.logistics.enums.RequestStatus;
import com.vibego.logistics.service.RequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    @Autowired
    private RequestService requestService;

    @PostMapping
    public ResponseEntity<EntityModel<RequestDto>> createRequest(@Valid @RequestBody RequestDto requestDto) {
        RequestDto created = requestService.createRequest(requestDto);
        EntityModel<RequestDto> resource = EntityModel.of(created);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RequestController.class).getRequestStatus(created.getId())).withSelfRel();
        Link statusLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RequestController.class).getRequestStatus(created.getId())).withRel("status");
        resource.add(selfLink, statusLink);
        return ResponseEntity.created(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RequestController.class).getRequestStatus(created.getId())).toUri()).body(resource);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<EntityModel<RequestDto>> getRequestStatus(@PathVariable Long id) {
        RequestDto dto = requestService.getRequestStatus(id);
        EntityModel<RequestDto> resource = EntityModel.of(dto);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RequestController.class).getRequestStatus(id)).withSelfRel();
        resource.add(selfLink);
        // Add other links e.g. to order if exists
        if (dto.getOrderId() != null) {
            Link orderLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).getOrder(dto.getOrderId())).withRel("order");
            resource.add(orderLink);
        }
        return ResponseEntity.ok(resource);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<EntityModel<RequestDto>> updateStatus(@PathVariable Long id, @RequestParam RequestStatus status) {
        RequestDto updated = requestService.updateRequestStatus(id, status);
        EntityModel<RequestDto> resource = EntityModel.of(updated);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RequestController.class).getRequestStatus(id)).withSelfRel();
        resource.add(selfLink);
        return ResponseEntity.ok(resource);
    }

    // Other endpoints following REST: GET /api/requests , etc.
    @GetMapping
    public ResponseEntity<List<EntityModel<RequestDto>>> getAllRequests() {
        // Implement as needed, for demo return empty or add
        List<EntityModel<RequestDto>> resources = List.of(); // placeholder
        return ResponseEntity.ok(resources);
    }
}
