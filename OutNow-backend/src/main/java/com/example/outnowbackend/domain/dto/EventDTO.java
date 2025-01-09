package com.example.outnowbackend.domain.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class EventDTO {
    @Id
    private Integer event_id;
    private String title;
    private String image_url;
    private String location;
    private Double price;
    private String attendees;
}

