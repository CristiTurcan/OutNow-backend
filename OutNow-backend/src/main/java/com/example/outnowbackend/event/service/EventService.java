package com.example.outnowbackend.event.service;

import com.example.outnowbackend.businessaccount.domain.BusinessAccount;
import com.example.outnowbackend.businessaccount.repository.BusinessAccountRepo;
import com.example.outnowbackend.event.domain.Event;
import com.example.outnowbackend.event.repository.EventRepo;
import com.example.outnowbackend.event.dto.EventDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepo eventRepo;
    private final BusinessAccountRepo businessAccountRepo;

    // Helper to convert an Event entity to a DTO
    private EventDTO convertToDTO(Event event) {
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
        return dto;
    }

    // Create a new event associated with a business account
    @Transactional
    public EventDTO createEvent(Event event, Integer businessAccountId) {
        Optional<BusinessAccount> account = businessAccountRepo.findById(businessAccountId);
        if (account.isEmpty()) {
            throw new RuntimeException("Business account not found");
        }
        event.setBusinessAccount(account.get());
        Event createdEvent = eventRepo.save(event);
        return convertToDTO(createdEvent);
    }

    // Retrieve all events
    public List<EventDTO> getAllEvents() {
        List<Event> events = eventRepo.findAll();
        return events.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Retrieve single event by id
    public Optional<EventDTO> getEventById(Integer eventId) {
        return eventRepo.findById(eventId).map(this::convertToDTO);
    }

    @Transactional
    public List<EventDTO> getEventsByBusinessAccount(Integer businessAccountId) {
        List<Event> events = eventRepo.findByBusinessAccount_Id(businessAccountId);
        return events.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Update existing event details
    @Transactional
    public EventDTO updateEvent(Integer eventId, Event updatedEvent) {
        Event event = eventRepo.findById(eventId).map(existing -> {
            existing.setTitle(updatedEvent.getTitle());
            existing.setDescription(updatedEvent.getDescription());
            existing.setImageUrl(updatedEvent.getImageUrl());
            existing.setLocation(updatedEvent.getLocation());
            existing.setPrice(updatedEvent.getPrice());
            return eventRepo.save(existing);
        }).orElseThrow(() -> new RuntimeException("Event not found"));
        return convertToDTO(event);
    }

    // Delete event by id
    @Transactional
    public void deleteEvent(Integer eventId) {
        eventRepo.deleteById(eventId);
    }
}
