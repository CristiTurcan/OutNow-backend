package com.example.outnowbackend.event.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "event")
public class Event {

    @Id
    private Integer event_id;
    private String title;
    @Column(columnDefinition = "text")
    private String image_url;
    private String location;
    private Double price;
    private String attendees;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

}
