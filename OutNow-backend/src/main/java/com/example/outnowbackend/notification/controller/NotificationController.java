package com.example.outnowbackend.notification.controller;

import com.example.outnowbackend.notification.domain.Notification;
import com.example.outnowbackend.notification.dto.NotificationDTO;
import com.example.outnowbackend.notification.mapper.NotificationMapper;
import com.example.outnowbackend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    /**
     * Get all notifications for a given user, newest first.
     */
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(
            @RequestParam Integer userId
    ) {
        List<NotificationDTO> dtos = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(dtos);
    }

    /**
     * Get count of unread notifications for a user.
     */
    @GetMapping("/unread/count")
    public ResponseEntity<Long> countUnread(
            @RequestParam Integer userId
    ) {
        long count = notificationService.countUnread(userId);
        return ResponseEntity.ok(count);
    }

    /**
     * Mark a specific notification as read.
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }
}

