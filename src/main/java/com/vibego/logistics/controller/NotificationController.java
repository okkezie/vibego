package com.vibego.logistics.controller;

import com.vibego.logistics.dto.NotificationDto;
import com.vibego.logistics.service.NotificationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<EntityModel<NotificationDto>>> getNotifications(@PathVariable @Positive Long driverId) {
        log.info("Received request for pending notifications of driver: {}", driverId);
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
        log.info("Returning {} pending notifications for driver: {}", resources.size(), driverId);
        return ResponseEntity.ok(resources);
    }

    @PostMapping("/{id}/respond")
    public ResponseEntity<EntityModel<NotificationDto>> respondToNotification(
            @PathVariable @Positive Long id,
            @RequestParam boolean accept,
            @RequestParam @Positive Long driverId) {
        log.info("Received response to notification ID: {} from driver {} (accept: {})", id, driverId, accept);
        NotificationDto updated = notificationService.respondToNotification(id, accept, driverId);
        EntityModel<NotificationDto> resource = EntityModel.of(updated);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NotificationController.class)
                .getNotifications(driverId)).withRel("notifications");
        resource.add(selfLink);
        log.info("Processed response for notification ID: {} (new status: {})", id, updated.getStatus());
        return ResponseEntity.ok(resource);
    }
}
