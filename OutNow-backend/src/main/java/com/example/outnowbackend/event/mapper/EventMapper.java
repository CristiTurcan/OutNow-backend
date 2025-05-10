package com.example.outnowbackend.event.mapper;

import com.example.outnowbackend.event.domain.Event;
import com.example.outnowbackend.event.dto.EventDTO;
import com.example.outnowbackend.feedback.domain.Feedback;
import com.example.outnowbackend.feedback.repository.FeedbackRepo;
import com.example.outnowbackend.user.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventMapper {

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private final FeedbackRepo feedbackRepo;
    private final UserRepo userRepo;

    public EventDTO toDTO(Event event) {
        if (event == null) {
            return null;
        }
        EventDTO dto = new EventDTO();
        dto.setEventId(event.getEventId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setImageUrl(event.getImageUrl());
        dto.setLocation(event.getLocation());
        dto.setPrice(event.getPrice());
        dto.setCreatedAt(event.getCreatedAt());
        dto.setUpdatedAt(event.getUpdatedAt());
        dto.setBusinessAccountId(event.getBusinessAccount() != null ? event.getBusinessAccount().getId() : null);
        dto.setEventDate(event.getEventDate() != null
                ? event.getEventDate().format(dateFormatter)
                : null);
        dto.setEventTime(event.getEventTime() != null
                ? event.getEventTime().format(timeFormatter)
                : null);
        dto.setInterestList(event.getInterestList());

        long favCount = userRepo.countByFavoritedEvents_EventId(event.getEventId());
        dto.setFavoriteCount((int) favCount);

        // Attendance count
        long attendCount = userRepo.countAttendeesByEventId(event.getEventId());
        dto.setAttendanceCount((int) attendCount);

        // Feedback list → reviewCount + averageRating
        List<Feedback> feedbacks = feedbackRepo.findByEvent(event);
        dto.setReviewCount(feedbacks.size());
        double avg = feedbacks.stream()
                .mapToDouble(f -> f.getRating().ordinal() + 1.0)
                .average()
                .orElse(0.0);
        dto.setAverageRating(avg);

        return dto;
    }
}
