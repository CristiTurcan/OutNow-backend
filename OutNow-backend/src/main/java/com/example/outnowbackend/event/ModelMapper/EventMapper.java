package com.example.outnowbackend.event.ModelMapper;

import com.example.outnowbackend.event.domain.Event;
import com.example.outnowbackend.event.domain.dto.EventDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    @Autowired
    private ModelMapper modelMapper;

    public EventDTO toDto (Event event) {
        return modelMapper.map(event, EventDTO.class);
    }

    public Event toEntity (EventDTO dto){
        return modelMapper.map(dto, Event.class);
    }
}
