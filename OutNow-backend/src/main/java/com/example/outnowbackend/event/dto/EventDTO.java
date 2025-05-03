package com.example.outnowbackend.event.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventDTO {
    private Integer eventId;
    private String title;
    private String description;
    private String imageUrl;
    private String location;
    private Double price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer businessAccountId;
    private String eventDate;
    private String eventTime;
    private String interestList;
}