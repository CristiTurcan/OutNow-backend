package com.example.outnowbackend.businessaccount.domain;

import com.example.outnowbackend.event.domain.Event;
import com.example.outnowbackend.user.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "business_account")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)   // <-- add this
public class BusinessAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "user_photo", columnDefinition = "text")
    private String userPhoto;

    @Column(length = 150)
    private String bio;

    private String location;

    @Column(name = "interest_list")
    private String interestList;

    // Relationship: each business account creates many events
    @OneToMany(mappedBy = "businessAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Event> createdEvents;

    @ManyToMany(mappedBy = "followedAccounts")
    private Set<User> followers = new HashSet<>();

}
