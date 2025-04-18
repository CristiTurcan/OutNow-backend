package com.example.outnowbackend.user.domain;

import com.example.outnowbackend.businessaccount.domain.BusinessAccount;
import com.example.outnowbackend.event.domain.Event;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "appuser")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Automatically generate IDs
    @EqualsAndHashCode.Include
    private Integer userid;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "user_photo", columnDefinition = "text")
    private String userPhoto;

    @Column(length = 150)
    private String bio;

    private String gender;

    @Column(name = "date_of_birth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private String location;

    @Column(name = "interest_list")
    private String interestList;


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

    @ManyToMany
    @JoinTable(
            name = "user_followed_business_accounts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "business_account_id")
    )
    private Set<BusinessAccount> followedAccounts = new HashSet<>();

}
