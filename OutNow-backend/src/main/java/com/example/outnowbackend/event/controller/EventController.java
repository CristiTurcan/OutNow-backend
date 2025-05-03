package com.example.outnowbackend.event.controller;

import com.example.outnowbackend.event.domain.Event;
import com.example.outnowbackend.event.dto.EventDTO;
import com.example.outnowbackend.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EventController {

    private final EventService eventService;

    @PostMapping("/{businessAccountId}")
    public ResponseEntity<EventDTO> createEvent(@RequestBody Event event,
                                                @PathVariable Integer businessAccountId) {
        EventDTO createdEvent = eventService.createEvent(event, businessAccountId);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Integer eventId) {
        Optional<EventDTO> eventDto = eventService.getEventById(eventId);
        return eventDto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/business/{businessAccountId}")
    public ResponseEntity<List<EventDTO>> getEventsByBusinessAccount(@PathVariable Integer businessAccountId) {
        List<EventDTO> events = eventService.getEventsByBusinessAccount(businessAccountId);
        return ResponseEntity.ok(events);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable Integer eventId,
                                                @RequestBody Event updatedEvent) {
        try {
            EventDTO eventDto = eventService.updateEvent(eventId, updatedEvent);
            return ResponseEntity.ok(eventDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{eventId}/attendees/count")
    public ResponseEntity<Long> getAttendeesCount(@PathVariable Integer eventId) {
        return ResponseEntity.ok(eventService.getAttendanceCount(eventId));
    }

    @GetMapping("/{eventId}/favorites/count")
    public ResponseEntity<Long> getFavoritesCount(@PathVariable Integer eventId) {
        return ResponseEntity.ok(eventService.getFavoriteCount(eventId));
    }

    @GetMapping("/{eventId}/favorites/unique-count")
    public ResponseEntity<Long> getUniqueFavoriteCount(@PathVariable Integer eventId) {
        return ResponseEntity.ok(eventService.getUniqueFavoriteCount(eventId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<EventDTO>> searchEvents(
            @RequestParam(required = false) String q
    ) {
        List<EventDTO> results = eventService.searchEvents(q);
        return ResponseEntity.ok(results);
    }


    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Integer eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }
}
