package com.vibego.logistics.controller;

import com.vibego.logistics.model.Vehicle;
import com.vibego.logistics.repository.VehicleRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private VehicleRepository vehicleRepository;

    @PostMapping
    public ResponseEntity<EntityModel<Vehicle>> createVehicle(@Valid @RequestBody Vehicle vehicle) {
        Vehicle saved = vehicleRepository.save(vehicle);
        EntityModel<Vehicle> resource = EntityModel.of(saved);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VehicleController.class).getVehicle(saved.getId())).withSelfRel());
        return ResponseEntity.ok(resource);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Vehicle>> getVehicle(@PathVariable Long id) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow();
        EntityModel<Vehicle> resource = EntityModel.of(vehicle);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VehicleController.class).getVehicle(id)).withSelfRel());
        return ResponseEntity.ok(resource);
    }

    @GetMapping
    public ResponseEntity<List<EntityModel<Vehicle>>> getAllVehicles() {
        List<EntityModel<Vehicle>> resources = vehicleRepository.findAll().stream()
                .map(vehicle -> {
                    EntityModel<Vehicle> em = EntityModel.of(vehicle);
                    em.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VehicleController.class).getVehicle(vehicle.getId())).withSelfRel());
                    return em;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }
}
