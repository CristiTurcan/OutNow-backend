package com.example.outnowbackend.notification.service;

import com.example.outnowbackend.notification.domain.Notification;
import com.example.outnowbackend.notification.domain.NotificationType;
import com.example.outnowbackend.notification.dto.NotificationDTO;
import com.example.outnowbackend.notification.mapper.NotificationMapper;
import com.example.outnowbackend.notification.repository.NotificationRepo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepo repo;
    private final NotificationMapper mapper;
    private final SimpMessagingTemplate messagingTemplate;


    @Transactional(readOnly = true)
    public List<NotificationDTO> getUserNotifications(Integer userId) {
        return repo
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }


    @Transactional(readOnly = true)
    public long countUnread(Integer userId) {
        return repo.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        repo.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            repo.save(n);
            long newCount = countUnread(n.getUserId());
            messagingTemplate.convertAndSend(
                    "/topic/unreadCount/" + n.getUserId(),
                    newCount
            );
        });
    }

    @Transactional
    public NotificationDTO createNotification(
            Integer userId,
            String title,
            String body,
            NotificationType type,
            Integer targetId
    ) {
        Notification n = Notification.builder()
                .userId(userId)
                .title(title)
                .body(body)
                .read(false)
                .notificationType(type)
                .targetId(targetId)
                .build();

        Notification saved = repo.save(n);
        NotificationDTO dto = mapper.toDTO(saved);

        long newCount = countUnread(userId);
        messagingTemplate.convertAndSend(
                "/topic/unreadCount/" + userId,
                newCount
        );
        messagingTemplate.convertAndSend(
                "/topic/newNotification/" + userId,
                dto
        );

        return dto;
    }

}
