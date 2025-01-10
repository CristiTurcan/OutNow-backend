package com.example.outnowbackend.user.domain;

import com.example.outnowbackend.event.domain.Event;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "appuser")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Automatically generate IDs
    private Integer userid;

    @Column(nullable = false, unique = true)
    private String email;

    // Relationship to store favorited events
    @ManyToMany
    @JoinTable(
            name = "user_favorited_events",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private Set<Event> favoritedEvents;

    // Relationship to store events the user is going to
    @ManyToMany
    @JoinTable(
            name = "user_going_events",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private Set<Event> goingEvents;
}
