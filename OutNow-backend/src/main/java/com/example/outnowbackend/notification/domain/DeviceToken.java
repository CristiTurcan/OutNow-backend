package com.example.outnowbackend.notification.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "device_token")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user this token belongs to
    @Column(nullable = false)
    private Integer userId;

    // The Expo push token string
    @Column(nullable = false, unique = true)
    private String token;

    // When it was registered
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
