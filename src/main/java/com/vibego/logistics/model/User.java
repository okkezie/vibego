package com.vibego.logistics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;
    private String password; // hashed
    @Column(unique = true, nullable = false)
    private String email;
    private String role; // e.g., "USER", "DRIVER", "ADMIN"

    private LocalDateTime createdAt = LocalDateTime.now();
}
