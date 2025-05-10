package com.example.outnowbackend.user.controller;

import com.example.outnowbackend.businessaccount.dto.BusinessAccountDTO;
import com.example.outnowbackend.event.dto.EventDTO;
import com.example.outnowbackend.user.domain.User;
import com.example.outnowbackend.user.dto.UserDTO;
import com.example.outnowbackend.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final ObjectMapper objectMapper;
    private final UserService userService;

    @GetMapping("/id-by-email")
    public ResponseEntity<Integer> getUserIdByEmail(@RequestParam String email) {
        Integer userId = userService.getUserIdByEmail(email);
        return userId != null ? ResponseEntity.ok(userId) : ResponseEntity.notFound().build();
    }

    @GetMapping("/by-email")
    public ResponseEntity<UserDTO> getUserByEmail(@RequestParam String email) {
        UserDTO userDto = userService.getUserByEmail(email);
        return userDto != null ? ResponseEntity.ok(userDto) : ResponseEntity.notFound().build();
    }

    @PostMapping("/upsert")
    public ResponseEntity<UserDTO> upsertUser(@RequestBody User user) {
        UserDTO updatedOrNewUser = userService.upsertUser(user);
        return ResponseEntity.ok(updatedOrNewUser);
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody String requestBody) throws JsonProcessingException {
        try {
            User user = objectMapper.readValue(requestBody, User.class);
            return ResponseEntity.ok(userService.updateUserProfile(user));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Invalid JSON: " + e.getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity<UserDTO> createUser(@RequestBody User user) {
        UserDTO createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer userId) {
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
    public ResponseEntity<?> addGoingEvent(
            @PathVariable Integer userId,
            @PathVariable Integer eventId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        try {
            userService.addGoingEvent(userId, eventId, quantity);
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
    public ResponseEntity<Set<EventDTO>> getUserFavorites(@PathVariable Integer userId) {
        Set<EventDTO> favorites = userService.getUserFavorites(userId);
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/{userId}/going")
    public ResponseEntity<Set<EventDTO>> getUserGoingEvents(@PathVariable Integer userId) {
        Set<EventDTO> going = userService.getUserGoingEvents(userId);
        return ResponseEntity.ok(going);
    }

    @PostMapping("/{userId}/follow/{businessAccountId}")
    public ResponseEntity<?> followBusinessAccount(
            @PathVariable Integer userId,
            @PathVariable Integer businessAccountId) {
        try {
            userService.followBusinessAccount(userId, businessAccountId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}/follow/{businessAccountId}")
    public ResponseEntity<?> unfollowBusinessAccount(
            @PathVariable Integer userId,
            @PathVariable Integer businessAccountId) {
        try {
            userService.unfollowBusinessAccount(userId, businessAccountId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}/followed-business-accounts")
    public ResponseEntity<Set<BusinessAccountDTO>> getFollowedBusinessAccounts(
            @PathVariable Integer userId) {
        try {
            Set<BusinessAccountDTO> followed = userService.getFollowedBusinessAccounts(userId);
            return ResponseEntity.ok(followed);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{userId}/visibility")
    public ResponseEntity<UserDTO> updateVisibilityFlags(
            @PathVariable Integer userId,
            @RequestBody VisibilityUpdateRequest body) {

        UserDTO dto = userService.updateVisibilityFlags(
                userId,
                body.showDob,
                body.showLocation,
                body.showGender,
                body.showInterests);
        return ResponseEntity.ok(dto);
    }

    public static class VisibilityUpdateRequest {
        @com.fasterxml.jackson.annotation.JsonProperty("showDob")
        public Boolean showDob;

        @com.fasterxml.jackson.annotation.JsonProperty("showLocation")
        public Boolean showLocation;

        @com.fasterxml.jackson.annotation.JsonProperty("showGender")
        public Boolean showGender;

        @com.fasterxml.jackson.annotation.JsonProperty("showInterests")
        public Boolean showInterests;
    }


}
