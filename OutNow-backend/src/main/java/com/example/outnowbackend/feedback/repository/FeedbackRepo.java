package com.example.outnowbackend.feedback.repository;

import com.example.outnowbackend.event.domain.Event;
import com.example.outnowbackend.feedback.domain.Feedback;
import com.example.outnowbackend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepo extends JpaRepository<Feedback, Long> {
    List<Feedback> findByEvent(Event event);

    Optional<Feedback> findByUserAndEvent(User user, Event event);
}
