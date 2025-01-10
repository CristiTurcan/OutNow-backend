package com.example.outnowbackend.user.controller;

import com.example.outnowbackend.user.domain.User;
import com.example.outnowbackend.user.service.UserService;
import com.example.outnowbackend.event.domain.Event;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/id-by-email")
    public ResponseEntity<Integer> getUserIdByEmail(@RequestParam String email) {
        Integer userId = userService.getUserIdByEmail(email);
        if (userId != null) {
            return ResponseEntity.ok(userId);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/upsert")
    public ResponseEntity<User> upsertUser(@RequestBody User user) {
        User updatedOrNewUser = userService.upsertUser(user);
        return ResponseEntity.ok(updatedOrNewUser);
    }

    @PostMapping("/")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Integer userId) {
        return userService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{userId}/favorites/{eventId}")
    public ResponseEntity<?> addFavoriteEvent(@PathVariable Integer userId, @PathVariable Integer eventId) {
        try {
            userService.addFavoriteEvent(userId, eventId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}/favorites/{eventId}")
    public ResponseEntity<?> removeFavoriteEvent(@PathVariable Integer userId, @PathVariable Integer eventId) {
        try {
            userService.removeFavoriteEvent(userId, eventId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{userId}/going/{eventId}")
    public ResponseEntity<?> addGoingEvent(@PathVariable Integer userId, @PathVariable Integer eventId) {
        try {
            userService.addGoingEvent(userId, eventId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}/going/{eventId}")
    public ResponseEntity<?> removeGoingEvent(@PathVariable Integer userId, @PathVariable Integer eventId) {
        try {
            userService.removeGoingEvent(userId, eventId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}/favorites")
    public ResponseEntity<Set<Event>> getUserFavorites(@PathVariable Integer userId) {
        Set<Event> favorites = userService.getUserFavorites(userId);
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/{userId}/going")
    public ResponseEntity<Set<Event>> getUserGoingEvents(@PathVariable Integer userId) {
        Set<Event> going = userService.getUserGoingEvents(userId);
        return ResponseEntity.ok(going);
    }
}
