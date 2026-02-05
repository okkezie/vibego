package com.vibego.logistics.controller;

import com.vibego.logistics.model.Driver;
import com.vibego.logistics.repository.DriverRepository;
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
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Slf4j
public class DriverController {

    private final DriverRepository driverRepository;

    @PostMapping
    public ResponseEntity<EntityModel<Driver>> createDriver(@Valid @RequestBody Driver driver) {
        log.info("Received request to create driver: {}", driver.getName());
        Driver saved = driverRepository.save(driver);
        EntityModel<Driver> resource = EntityModel.of(saved);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DriverController.class).getDriver(saved.getId())).withSelfRel());
        log.info("Successfully created driver ID: {}", saved.getId());
        return ResponseEntity.ok(resource);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Driver>> getDriver(@PathVariable Long id) {
        log.info("Received request for driver details ID: {}", id);
        Driver driver = driverRepository.findById(id).orElseThrow();
        EntityModel<Driver> resource = EntityModel.of(driver);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DriverController.class).getDriver(id)).withSelfRel());
        log.info("Returning details for driver ID: {}", id);
        return ResponseEntity.ok(resource);
    }

    @GetMapping
    public ResponseEntity<List<EntityModel<Driver>>> getAllDrivers() {
        log.info("Received request for all drivers");
        List<EntityModel<Driver>> resources = driverRepository.findAll().stream()
                .map(driver -> {
                    EntityModel<Driver> em = EntityModel.of(driver);
                    em.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DriverController.class).getDriver(driver.getId())).withSelfRel());
                    return em;
                })
                .collect(Collectors.toList());
        log.info("Returning {} drivers", resources.size());
        return ResponseEntity.ok(resources);
    }
}
