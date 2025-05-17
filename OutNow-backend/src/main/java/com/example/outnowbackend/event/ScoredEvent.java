package com.example.outnowbackend.event;

import com.example.outnowbackend.event.dto.EventDTO;
import lombok.Getter;

@Getter
public class ScoredEvent {
    private final EventDTO event;
    public double interestScore, timeScore, popRaw, popScore, ratingScore, rawScore;
    public double locationScore;

    public ScoredEvent(EventDTO e) {
        this.event = e;
    }
}