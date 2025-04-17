package com.example.outnowbackend.feedback.service;

import com.example.outnowbackend.feedback.domain.Feedback;
import com.example.outnowbackend.feedback.domain.Rating;
import com.example.outnowbackend.feedback.dto.FeedbackDTO;
import com.example.outnowbackend.feedback.mapper.FeedbackMapper;
import com.example.outnowbackend.feedback.repository.FeedbackRepo;
import com.example.outnowbackend.event.domain.Event;
import com.example.outnowbackend.event.repository.EventRepo;
import com.example.outnowbackend.user.domain.User;
import com.example.outnowbackend.user.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepo feedbackRepo;
    private final EventRepo eventRepo;
    private final UserRepo userRepo;
    private final FeedbackMapper feedbackMapper;

    @Transactional
    public FeedbackDTO addFeedback(Integer eventId, Integer userId, String ratingStr, String comment) {
        // Convert rating string to enum, etc.
        Rating rating = Rating.valueOf(ratingStr.toUpperCase().replace(" ", "_"));

        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if feedback already exists for this user and event
        Optional<Feedback> existingFeedback = feedbackRepo.findByUserAndEvent(user, event);
        if(existingFeedback.isPresent()){
            throw new RuntimeException("Feedback for this event has already been submitted.");
        }

        Feedback feedback = new Feedback();
        feedback.setEvent(event);
        feedback.setUser(user);
        feedback.setRating(rating);
        feedback.setComment(comment);
        Feedback savedFeedback = feedbackRepo.save(feedback);
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
