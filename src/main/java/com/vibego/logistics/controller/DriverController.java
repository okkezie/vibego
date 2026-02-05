package com.vibego.logistics.controller;

import com.vibego.logistics.model.Driver;
import com.vibego.logistics.repository.DriverRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    @Autowired
    private DriverRepository driverRepository;

    @PostMapping
    public ResponseEntity<EntityModel<Driver>> createDriver(@Valid @RequestBody Driver driver) {
        Driver saved = driverRepository.save(driver);
        EntityModel<Driver> resource = EntityModel.of(saved);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DriverController.class).getDriver(saved.getId())).withSelfRel());
        return ResponseEntity.ok(resource);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Driver>> getDriver(@PathVariable Long id) {
        Driver driver = driverRepository.findById(id).orElseThrow();
        EntityModel<Driver> resource = EntityModel.of(driver);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DriverController.class).getDriver(id)).withSelfRel());
        return ResponseEntity.ok(resource);
    }

    @GetMapping
    public ResponseEntity<List<EntityModel<Driver>>> getAllDrivers() {
        List<EntityModel<Driver>> resources = driverRepository.findAll().stream()
                .map(driver -> {
                    EntityModel<Driver> em = EntityModel.of(driver);
                    em.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DriverController.class).getDriver(driver.getId())).withSelfRel());
                    return em;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }
}
