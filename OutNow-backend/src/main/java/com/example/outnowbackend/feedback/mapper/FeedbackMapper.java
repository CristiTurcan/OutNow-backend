package com.example.outnowbackend.feedback.mapper;

import com.example.outnowbackend.feedback.domain.Feedback;
import com.example.outnowbackend.feedback.dto.FeedbackDTO;
import org.springframework.stereotype.Component;

@Component
public class FeedbackMapper {
    public FeedbackDTO toDTO(Feedback feedback) {
        if (feedback == null) {
            return null;
        }
        FeedbackDTO dto = new FeedbackDTO();
        dto.setFeedbackId(feedback.getFeedbackId());
        dto.setEventId(feedback.getEvent() != null ? feedback.getEvent().getEventId() : null);
        dto.setUserId(feedback.getUser() != null ? feedback.getUser().getUserid() : null);
        dto.setRating(feedback.getRating().name());
        dto.setComment(feedback.getComment());
        dto.setCreatedAt(feedback.getCreatedAt());
        return dto;
    }
}
