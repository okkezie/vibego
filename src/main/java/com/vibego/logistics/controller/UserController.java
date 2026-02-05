package com.vibego.logistics.controller;

import com.vibego.logistics.model.User;
import com.vibego.logistics.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<EntityModel<User>> register(@RequestBody User user) {
        log.info("Received registration request for user: {}", user.getUsername());
        User saved = userService.register(user);
        EntityModel<User> resource = EntityModel.of(saved);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).register(user)).withSelfRel());
        log.info("Successfully registered user: {}", saved.getUsername());
        return ResponseEntity.ok(resource);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User loginUser) {
        log.info("Received login attempt for username: {}", loginUser.getUsername());
        String token = userService.login(loginUser.getUsername(), loginUser.getPassword());
        if (token != null) {
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("username", loginUser.getUsername());
            log.info("Successful login for user: {}", loginUser.getUsername());
            return ResponseEntity.ok(response);
        }
        log.warn("Failed login attempt for user: {}", loginUser.getUsername());
        return ResponseEntity.status(401).build();
    }
}
