package com.example.outnowbackend.event.controller;

import com.example.outnowbackend.event.domain.dto.EventDTO;
import com.example.outnowbackend.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/event/v1")
@RequiredArgsConstructor
@Validated
public class EventController {

    private final EventService eventService;
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    /**
     * This method is called when a GET request is made
     * URL: localhost:8080/event/v1/
     */
    @GetMapping("/")
    public ResponseEntity<List<EventDTO>> getAllEvents(){
        return ResponseEntity.ok().body(eventService.getAllEvents());
    }

    /**
     * This method is called when a GET request is made
     * URL: localhost:8080/employee/v1/1 (or any other id)
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Integer id) {
        return ResponseEntity.ok().body(eventService.getEventById((id)));
    }

    /**
     * This method is called when a POST request is made
     * URL: localhost:8080/employee/v1/
     */
    @PostMapping("/")
    public ResponseEntity<EventDTO> saveEvent(@RequestBody EventDTO eventDto) {
        logger.debug("Received event: {}", eventDto);
        return ResponseEntity.ok().body(eventService.saveEvent(eventDto));
    }

    /**
     * This method is called when a PUT request is made
     * URL: localhost:8080/employee/v1/
     */
    @PutMapping("/")
    public ResponseEntity<EventDTO> updateEvent(@RequestBody EventDTO eventDto) {
        return ResponseEntity.ok().body(eventService.updateEvent(eventDto));
    }

    /**
     * This method is called when a PUT request is made
     * URL: localhost:8080/employee/v1/1 (or any other id)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable Integer id) {
        eventService.deleteEventById(id);
        return ResponseEntity.ok().body("Deleted event successfully");
    }
}
