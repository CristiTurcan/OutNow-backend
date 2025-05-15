package com.example.outnowbackend.notification.controller;

import com.example.outnowbackend.notification.service.DeviceTokenService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/devices")
@CrossOrigin(origins = "*")
public class DeviceTokenController {

    private final DeviceTokenService service;

    public DeviceTokenController(DeviceTokenService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> register(@RequestBody RegisterRequest req) {
        service.registerToken(req.getUserId(), req.getToken());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{token}")
    public ResponseEntity<Void> unregister(@PathVariable String token) {
        service.unregisterToken(token);
        return ResponseEntity.noContent().build();
    }

    @Data
    public static class RegisterRequest {
        private Integer userId;
        private String token;
    }
}
