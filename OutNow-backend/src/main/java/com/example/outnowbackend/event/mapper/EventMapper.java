package com.example.outnowbackend.event.mapper;

import com.example.outnowbackend.event.domain.Event;
import com.example.outnowbackend.event.dto.EventDTO;
import org.springframework.stereotype.Component;
import java.time.format.DateTimeFormatter;

@Component
public class EventMapper {

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

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
        return dto;
    }

    // Optionally, provide a method to convert a DTO back to an entity if necessary.
}
