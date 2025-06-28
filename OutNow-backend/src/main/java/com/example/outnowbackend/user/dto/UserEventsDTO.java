package com.example.outnowbackend.user.dto;

import com.example.outnowbackend.event.dto.EventDTO;

import java.util.Set;

public class UserEventsDTO {
    private Set<EventDTO> favorites;
    private Set<EventDTO> going;

    public UserEventsDTO(Set<EventDTO> favorites, Set<EventDTO> going) {
        this.favorites = favorites;
        this.going = going;
    }

    public Set<EventDTO> getFavorites() {
        return favorites;
    }

    public Set<EventDTO> getGoing() {
        return going;
    }
}
