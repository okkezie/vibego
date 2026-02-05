package com.vibego.logistics.service;

import com.vibego.logistics.config.JwtUtil;
import com.vibego.logistics.model.User;
import com.vibego.logistics.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    @Transactional
    public User register(User user) {
        log.info("Processing registration for username: {}", user.getUsername());
        // Ensure transient (prevent ID=0 stale entity error)
        user.setId(null);
        // Prevent duplicates
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            log.warn("Registration failed - username already exists: {}", user.getUsername());
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            log.warn("Registration failed - email already exists: {}", user.getEmail());
            throw new RuntimeException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);
        log.info("Successfully registered user ID: {}", saved.getId());
        return saved;
    }

    public String login(String username, String password) {
        log.info("Processing login attempt for username: {}", username);
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            String token = jwtUtil.generateToken(username);
            log.info("Successful login for user: {}", username);
            return token;
        }
        log.warn("Failed login attempt for username: {}", username);
        return null;
    }
}
