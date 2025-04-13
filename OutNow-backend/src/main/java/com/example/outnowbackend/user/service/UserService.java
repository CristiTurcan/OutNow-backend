package com.example.outnowbackend.user.service;

import com.example.outnowbackend.event.dto.EventDTO;
import com.example.outnowbackend.event.mapper.EventMapper;
import com.example.outnowbackend.user.domain.User;
import com.example.outnowbackend.user.dto.UserDTO;
import com.example.outnowbackend.user.mapper.UserMapper;
import com.example.outnowbackend.user.repository.UserRepo;
import com.example.outnowbackend.event.domain.Event;
import com.example.outnowbackend.event.repository.EventRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final EventRepo eventRepo;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final EventMapper eventMapper;  // Injected mapper
    private final UserMapper userMapper;


    // Conversion helper: entity -> DTO
//    private UserDTO convertToDTO(User user) {
//        UserDTO dto = new UserDTO();
//        dto.setUserid(user.getUserid());
//        dto.setEmail(user.getEmail());
//        dto.setUsername(user.getUsername());
//        dto.setUserPhoto(user.getUserPhoto());
//        dto.setBio(user.getBio());
//        dto.setGender(user.getGender());
//        dto.setDateOfBirth(user.getDateOfBirth().format(DateTimeFormatter.ISO_LOCAL_DATE));
//        dto.setLocation(user.getLocation());
//        dto.setInterestList(user.getInterestList());
//        return dto;
//    }

    @Transactional
    public UserDTO createUser(User user) {
        User created = userRepo.save(user);
        return userMapper.toDTO(created);
    }

    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserById(Integer userId) {
        return userRepo.findById(userId)
                .map(userMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        User user = userRepo.findByEmail(email);
        return (user != null) ? userMapper.toDTO(user) : null;
    }

    public Integer getUserIdByEmail(String email) {
        User user = userRepo.findByEmail(email);
        return user != null ? user.getUserid() : null;
    }

    @Transactional
    public void addFavoriteEvent(Integer userId, Integer eventId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        user.getFavoritedEvents().add(event);
        userRepo.save(user);
    }



    @Transactional
    public void removeFavoriteEvent(Integer userId, Integer eventId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        // Remove the event from the in-memory collection
        boolean removed = user.getFavoritedEvents().remove(event);
        if (removed) {
            userRepo.save(user);
            logger.info("Event (ID: {}) removed from favorites for user (ID: {}).", eventId, userId);
        } else {
            logger.warn("Event (ID: {}) was not present in favorites for user (ID: {}).", eventId, userId);
        }
    }

    @Transactional
    public void addGoingEvent(Integer userId, Integer eventId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        // Optional: check to avoid duplicates
        if (!user.getGoingEvents().contains(event)) {
            user.getGoingEvents().add(event);
            userRepo.save(user);
            logger.info("Event (ID: {}) added to going events for user (ID: {}).", eventId, userId);
        } else {
            logger.warn("Event (ID: {}) is already in going events for user (ID: {}).", eventId, userId);
        }
    }


    @Transactional
    public void removeGoingEvent(Integer userId, Integer eventId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        boolean removed = user.getGoingEvents().remove(event);
        if (removed) {
            userRepo.save(user);
            logger.info("Event (ID: {}) removed from going events for user (ID: {}).", eventId, userId);
        } else {
            logger.warn("Event (ID: {}) was not present in going events for user (ID: {}).", eventId, userId);
        }
    }


    @Transactional(readOnly = true)
    public Set<EventDTO> getUserFavorites(Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        // Force initialization if lazy-loaded
        user.getFavoritedEvents().size();
        // Convert the favorites to DTOs
        return user.getFavoritedEvents().stream()
                .map(eventMapper::toDTO)
                .collect(Collectors.toSet());
    }



    @Transactional(readOnly = true)
    public Set<EventDTO> getUserGoingEvents(Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        // Force initialization if needed
        user.getGoingEvents().size();

        // Convert each Event to an EventDTO
        return user.getGoingEvents().stream()
                .map(eventMapper::toDTO)
                .collect(Collectors.toSet());
    }



    @Transactional
    public UserDTO upsertUser(User user) {
        try {
            logger.debug("Attempting to upsert user with email: {}", user.getEmail());
            User existingUser = userRepo.findByEmail(user.getEmail());
            if (existingUser != null) {
                logger.debug("User exists, updating user: {}", existingUser);
                return userMapper.toDTO(userRepo.save(existingUser));
            } else {
                logger.debug("User does not exist, creating new user");
                return userMapper.toDTO(userRepo.save(user));
            }
        } catch (Exception e) {
            logger.error("Error upserting user with email {}: {}", user.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to upsert user", e);
        }
    }

    @Transactional
    public UserDTO updateUserProfile(User user) {
        User existingUser = userRepo.findByEmail(user.getEmail());
        if (existingUser == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (user.getUsername() != null) {
            existingUser.setUsername(user.getUsername());
        }
        if (user.getUserPhoto() != null) {
            existingUser.setUserPhoto(user.getUserPhoto());
        }
        if (user.getBio() != null) {
            existingUser.setBio(user.getBio());
        }
        if (user.getGender() != null) {
            existingUser.setGender(user.getGender());
        }
        if (user.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(user.getDateOfBirth());
        }
        if (user.getLocation() != null) {
            existingUser.setLocation(user.getLocation());
        }
        if (user.getInterestList() != null) {
            existingUser.setInterestList(user.getInterestList());
        }
        return userMapper.toDTO(userRepo.save(existingUser));
    }
}
