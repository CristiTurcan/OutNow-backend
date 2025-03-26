package com.example.outnowbackend.event.service;

import com.example.outnowbackend.businessaccount.domain.BusinessAccount;
import com.example.outnowbackend.businessaccount.repository.BusinessAccountRepo;
import com.example.outnowbackend.event.domain.Event;
import com.example.outnowbackend.event.repository.EventRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepo eventRepo;
    private final BusinessAccountRepo businessAccountRepo;

    // Create a new event associated with a business account
    @Transactional
    public Event createEvent(Event event, Integer businessAccountId) {
        Optional<BusinessAccount> account = businessAccountRepo.findById(businessAccountId);
        if (account.isEmpty()) {
            throw new RuntimeException("Business account not found");
        }
        event.setBusinessAccount(account.get());
        return eventRepo.save(event);
    }

    // Retrieve all events
    public List<Event> getAllEvents() {
        return eventRepo.findAll();
    }

    // Retrieve single event by id
    public Optional<Event> getEventById(Integer eventId) {
        return eventRepo.findById(eventId);
    }

    @Transactional
    public List<Event> getEventsByBusinessAccount(Integer businessAccountId) {
        return eventRepo.findByBusinessAccount_Id(businessAccountId);
    }


    // Update existing event details
    @Transactional
    public Event updateEvent(Integer eventId, Event updatedEvent) {
        return eventRepo.findById(eventId).map(event -> {
            event.setTitle(updatedEvent.getTitle());
            event.setDescription(updatedEvent.getDescription());
            event.setImageUrl(updatedEvent.getImageUrl());
            event.setLocation(updatedEvent.getLocation());
            event.setPrice(updatedEvent.getPrice());
            return eventRepo.save(event);
        }).orElseThrow(() -> new RuntimeException("Event not found"));
    }

    // Delete event by id
    @Transactional
    public void deleteEvent(Integer eventId) {
        eventRepo.deleteById(eventId);
    }
}
