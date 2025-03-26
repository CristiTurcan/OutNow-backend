package com.example.outnowbackend.event.controller;

import com.example.outnowbackend.event.domain.Event;
import com.example.outnowbackend.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EventController {

    private final EventService eventService;

    // Create a new event
    @PostMapping("/{businessAccountId}")
    public ResponseEntity<Event> createEvent(@RequestBody Event event,
                                             @PathVariable Integer businessAccountId) {
        Event createdEvent = eventService.createEvent(event, businessAccountId);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    // Get all events
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    // Get event by id
    @GetMapping("/{eventId}")
    public ResponseEntity<Event> getEventById(@PathVariable Integer eventId) {
        return eventService.getEventById(eventId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/business/{businessAccountId}")
    public ResponseEntity<List<Event>> getEventsByBusinessAccount(@PathVariable Integer businessAccountId) {
        List<Event> events = eventService.getEventsByBusinessAccount(businessAccountId);
        return ResponseEntity.ok(events);
    }

    // Update event by id
    @PutMapping("/{eventId}")
    public ResponseEntity<Event> updateEvent(@PathVariable Integer eventId,
                                             @RequestBody Event updatedEvent) {
        try {
            Event event = eventService.updateEvent(eventId, updatedEvent);
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete event by id
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Integer eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }
}
