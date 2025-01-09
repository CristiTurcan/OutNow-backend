package com.example.outnowbackend.controller;

import com.example.outnowbackend.entity.Event;
import com.example.outnowbackend.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/event/v1")
@RequiredArgsConstructor
@Validated
public class EventController {

    private final EventService eventService;

    /**
     * This method is called when a GET request is made
     * URL: localhost:8080/event/v1/
     */
    @GetMapping("/")
    public ResponseEntity<List<Event>> getAllEvents(){
        return ResponseEntity.ok().body(eventService.getAllEvents());
    }

    /**
     * This method is called when a GET request is made
     * URL: localhost:8080/employee/v1/1 (or any other id)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Integer id) {
        return ResponseEntity.ok().body(eventService.getEventById((id)));
    }

    /**
     * This method is called when a POST request is made
     * URL: localhost:8080/employee/v1/
     */
    @PostMapping("/")
    public ResponseEntity<Event> saveEvent(@RequestBody Event event) {
        return ResponseEntity.ok().body(eventService.saveEvent(event));
    }

    /**
     * This method is called when a PUT request is made
     * URL: localhost:8080/employee/v1/
     */
    @PutMapping("/")
    public ResponseEntity<Event> updateEvent(@RequestBody Event event) {
        return ResponseEntity.ok().body(eventService.updateEvent(event));
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
