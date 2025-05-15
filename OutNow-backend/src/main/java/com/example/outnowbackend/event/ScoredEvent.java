package com.example.outnowbackend.event;

import com.example.outnowbackend.event.dto.EventDTO;
import lombok.Getter;

public class ScoredEvent {
    @Getter
    private final EventDTO event;
    public double interestScore, timeScore, popRaw, popScore, ratingScore, rawScore;

    public ScoredEvent(EventDTO e) {
        this.event = e;
    }
}