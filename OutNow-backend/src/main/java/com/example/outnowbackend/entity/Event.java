package com.example.outnowbackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "event")
public class Event {

    @Id
    private Integer event_id;
    private String title;
    private String image_url;
    private String location;
    private Double price;
    private String attendees;
}