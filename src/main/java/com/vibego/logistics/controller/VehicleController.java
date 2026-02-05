package com.vibego.logistics.controller;

import com.vibego.logistics.model.Vehicle;
import com.vibego.logistics.repository.VehicleRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@Slf4j
public class VehicleController {

    private final VehicleRepository vehicleRepository;

    @PostMapping
    public ResponseEntity<EntityModel<Vehicle>> createVehicle(@Valid @RequestBody Vehicle vehicle) {
        log.info("Received request to create vehicle: {}", vehicle.getName());
        Vehicle saved = vehicleRepository.save(vehicle);
        EntityModel<Vehicle> resource = EntityModel.of(saved);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VehicleController.class).getVehicle(saved.getId())).withSelfRel());
        log.info("Successfully created vehicle ID: {}", saved.getId());
        return ResponseEntity.ok(resource);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Vehicle>> getVehicle(@PathVariable Long id) {
        log.info("Received request for vehicle details ID: {}", id);
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow();
        EntityModel<Vehicle> resource = EntityModel.of(vehicle);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VehicleController.class).getVehicle(id)).withSelfRel());
        log.info("Returning details for vehicle ID: {}", id);
        return ResponseEntity.ok(resource);
    }

    @GetMapping
    public ResponseEntity<List<EntityModel<Vehicle>>> getAllVehicles() {
        log.info("Received request for all vehicles");
        List<EntityModel<Vehicle>> resources = vehicleRepository.findAll().stream()
                .map(vehicle -> {
                    EntityModel<Vehicle> em = EntityModel.of(vehicle);
                    em.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VehicleController.class).getVehicle(vehicle.getId())).withSelfRel());
                    return em;
                })
                .collect(Collectors.toList());
        log.info("Returning {} vehicles", resources.size());
        return ResponseEntity.ok(resources);
    }
}
