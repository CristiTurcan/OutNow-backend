package com.example.outnowbackend.feedback.service;

import com.example.outnowbackend.event.domain.Event;
import com.example.outnowbackend.event.repository.EventRepo;
import com.example.outnowbackend.feedback.domain.Feedback;
import com.example.outnowbackend.feedback.domain.Rating;
import com.example.outnowbackend.feedback.dto.FeedbackDTO;
import com.example.outnowbackend.feedback.mapper.FeedbackMapper;
import com.example.outnowbackend.feedback.repository.FeedbackRepo;
import com.example.outnowbackend.notification.domain.NotificationType;
import com.example.outnowbackend.notification.service.DeviceTokenService;
import com.example.outnowbackend.notification.service.NotificationService;
import com.example.outnowbackend.notification.service.PushNotificationService;
import com.example.outnowbackend.user.domain.User;
import com.example.outnowbackend.user.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepo feedbackRepo;
    private final EventRepo eventRepo;
    private final UserRepo userRepo;
    private final FeedbackMapper feedbackMapper;
    private final NotificationService notificationService;
    private final DeviceTokenService tokens;
    private final PushNotificationService push;

    @Transactional
    public FeedbackDTO addFeedback(Integer eventId, Integer userId, String ratingStr, String comment) {
        Rating rating = Rating.valueOf(ratingStr.toUpperCase().replace(" ", "_"));

        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Feedback> existingFeedback = feedbackRepo.findByUserAndEvent(user, event);
        if (existingFeedback.isPresent()) {
            throw new RuntimeException("Feedback for this event has already been submitted.");
        }

        Feedback feedback = new Feedback();
        feedback.setEvent(event);
        feedback.setUser(user);
        feedback.setRating(rating);
        feedback.setComment(comment);
        Feedback savedFeedback = feedbackRepo.save(feedback);

        Integer organizerId = savedFeedback.getEvent().getBusinessAccount().getId();
        String eventTitle = savedFeedback.getEvent().getTitle();
        String title = "New feedback on \"" + eventTitle + "\"";
        String body =
                "Someone rated your event " +
                        savedFeedback.getRating().name().toLowerCase().replace("_", " ") +
                        (comment != null && !comment.isBlank()
                                ? " and said: \"" + comment + "\""
                                : ".");

        notificationService.createNotification(organizerId, title, body, NotificationType.NEW_FEEDBACK, event.getEventId());

        List<String> pushTokens = tokens.getTokensForUser(organizerId);
        if (!pushTokens.isEmpty()) {
            push.sendPush(
                    pushTokens,
                    title,
                    body,
                    Map.of("eventId", savedFeedback.getEvent().getEventId())
            );
        }

        return feedbackMapper.toDTO(savedFeedback);
    }


    @Transactional(readOnly = true)
    public List<FeedbackDTO> getFeedbackForEvent(Integer eventId) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        return feedbackRepo.findByEvent(event)
                .stream()
                .map(feedbackMapper::toDTO)
                .collect(Collectors.toList());
    }
}
