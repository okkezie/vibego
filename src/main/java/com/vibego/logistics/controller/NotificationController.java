package com.vibego.logistics.controller;

import com.vibego.logistics.dto.NotificationDto;
import com.vibego.logistics.service.NotificationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@Validated
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<EntityModel<NotificationDto>>> getNotifications(@PathVariable @Positive Long driverId) {
        List<NotificationDto> notifications = notificationService.getPendingNotifications(driverId);
        List<EntityModel<NotificationDto>> resources = notifications.stream()
                .map(dto -> {
                    EntityModel<NotificationDto> resource = EntityModel.of(dto);
                    Link acceptLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NotificationController.class)
                            .respondToNotification(dto.getId(), true, driverId)).withRel("accept");
                    Link declineLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NotificationController.class)
                            .respondToNotification(dto.getId(), false, driverId)).withRel("decline");
                    resource.add(acceptLink, declineLink);
                    return resource;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    @PostMapping("/{id}/respond")
    public ResponseEntity<EntityModel<NotificationDto>> respondToNotification(
            @PathVariable @Positive Long id,
            @RequestParam boolean accept,
            @RequestParam @Positive Long driverId) {
        NotificationDto updated = notificationService.respondToNotification(id, accept, driverId);
        EntityModel<NotificationDto> resource = EntityModel.of(updated);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NotificationController.class)
                .getNotifications(driverId)).withRel("notifications");
        resource.add(selfLink);
        return ResponseEntity.ok(resource);
    }
}
