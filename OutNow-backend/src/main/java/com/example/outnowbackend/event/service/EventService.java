package com.example.outnowbackend.event.service;

import com.example.outnowbackend.businessaccount.domain.BusinessAccount;
import com.example.outnowbackend.businessaccount.repository.BusinessAccountRepo;
import com.example.outnowbackend.event.domain.Event;
import com.example.outnowbackend.event.mapper.EventMapper;
import com.example.outnowbackend.event.repository.EventRepo;
import com.example.outnowbackend.event.dto.EventDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepo eventRepo;
    private final BusinessAccountRepo businessAccountRepo;
    private final EventMapper eventMapper;

//    // Helper to convert an Event entity to a DTO
//    private EventDTO convertToDTO(Event event) {
//        EventDTO dto = new EventDTO();
//        dto.setEventId(event.getEventId());
//        dto.setTitle(event.getTitle());
//        dto.setDescription(event.getDescription());
//        dto.setImageUrl(event.getImageUrl());
//        dto.setLocation(event.getLocation());
//        dto.setPrice(event.getPrice());
//        dto.setCreatedAt(event.getCreatedAt());
//        dto.setUpdatedAt(event.getUpdatedAt());
//        dto.setBusinessAccountId(event.getBusinessAccount() != null ? event.getBusinessAccount().getId() : null);
//        dto.setEventDate(event.getEventDate() != null
//                ? event.getEventDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
//                : null);
//        dto.setEventTime(event.getEventTime() != null
//                ? event.getEventTime().format(DateTimeFormatter.ofPattern("HH:mm"))
//                : null);
//        dto.setInterestList(event.getInterestList());
//        return dto;
//    }

    // Create a new event associated with a business account
    @Transactional
    public EventDTO createEvent(Event event, Integer businessAccountId) {
        Optional<BusinessAccount> account = businessAccountRepo.findById(businessAccountId);
        if (account.isEmpty()) {
            throw new RuntimeException("Business account not found");
        }
        event.setBusinessAccount(account.get());
        Event createdEvent = eventRepo.save(event);
        return eventMapper.toDTO(createdEvent);
    }

    // Retrieve all events
    public List<EventDTO> getAllEvents() {
        return eventRepo.findAll().stream()
                .map(eventMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<EventDTO> getEventById(Integer eventId) {
        return eventRepo.findById(eventId)
                .map(eventMapper::toDTO);
    }


    @Transactional
    public List<EventDTO> getEventsByBusinessAccount(Integer businessAccountId) {
        return eventRepo.findByBusinessAccount_Id(businessAccountId)
                .stream()
                .map(eventMapper::toDTO)
                .collect(Collectors.toList());
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
            existing.setEventDate(updatedEvent.getEventDate());
            existing.setEventTime(updatedEvent.getEventTime());
            existing.setInterestList(updatedEvent.getInterestList());

            return eventRepo.save(existing);
        }).orElseThrow(() -> new RuntimeException("Event not found"));
        return eventMapper.toDTO(event);
    }



    // Delete event by id
    @Transactional
    public void deleteEvent(Integer eventId) {
        eventRepo.deleteById(eventId);
    }
}
