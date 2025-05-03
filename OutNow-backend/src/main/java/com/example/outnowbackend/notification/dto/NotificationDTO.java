package com.example.outnowbackend.notification.dto;

import com.example.outnowbackend.notification.domain.NotificationType;
import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private Integer userId;
    private String title;
    private String body;
    private Boolean read;
    private String createdAt;
    private NotificationType notificationType;
    private Integer targetId;
}