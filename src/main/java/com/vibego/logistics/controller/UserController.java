package com.vibego.logistics.controller;

import com.vibego.logistics.model.User;
import com.vibego.logistics.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<EntityModel<User>> register(@RequestBody User user) {
        User saved = userService.register(user);
        EntityModel<User> resource = EntityModel.of(saved);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).register(user)).withSelfRel());
        return ResponseEntity.ok(resource);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User loginUser) {
        String token = userService.login(loginUser.getUsername(), loginUser.getPassword());
        if (token != null) {
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("username", loginUser.getUsername());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).build();
    }
}
