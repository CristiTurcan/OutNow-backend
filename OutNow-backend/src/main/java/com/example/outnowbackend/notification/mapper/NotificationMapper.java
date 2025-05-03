package com.example.outnowbackend.notification.mapper;

import com.example.outnowbackend.notification.domain.Notification;
import com.example.outnowbackend.notification.dto.NotificationDTO;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class NotificationMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public NotificationDTO toDTO(Notification entity) {
        if (entity == null) {
            return null;
        }
        NotificationDTO dto = new NotificationDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setTitle(entity.getTitle());
        dto.setBody(entity.getBody());
        dto.setRead(entity.getRead());
        if (entity.getCreatedAt() != null) {
            dto.setCreatedAt(entity.getCreatedAt().format(FORMATTER));
        }
        dto.setNotificationType(entity.getNotificationType());
        dto.setTargetId(entity.getTargetId());
        return dto;
    }
}

