package com.example.outnowbackend.service;

import com.example.outnowbackend.ModelMapper.EventMapper;
import com.example.outnowbackend.domain.Event;
import com.example.outnowbackend.domain.dto.EventDTO;
import com.example.outnowbackend.repository.EventRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventRepo eventRepo;
    private final EventMapper eventMapper;

    public List<EventDTO> getAllEvents(){
        List<Event> events = eventRepo.findAll();
        return events.stream().map(eventMapper::toDto).collect(Collectors.toList());
    }

    public EventDTO getEventById(Integer id){
        Optional<Event> optionalEvent = eventRepo.findById(id);
        return optionalEvent.map(eventMapper::toDto).orElse(null);

    }

    public EventDTO saveEvent(EventDTO eventDTO) {
        Event event = eventMapper.toEntity(eventDTO);
        event.setCreated_at(LocalDateTime.now());
        event.setUpdated_at(LocalDateTime.now());
        Event savedEvent = eventRepo.save(event);
        log.info("Event with id: {} saved successfully", savedEvent.getEvent_id());
        return eventMapper.toDto(savedEvent);
    }


    public EventDTO updateEvent(EventDTO eventDTO) {
        Optional<Event> existingEvent = eventRepo.findById(eventDTO.getEvent_id());
        if (existingEvent.isPresent()) {
            Event event = existingEvent.get();
            event.setUpdated_at(LocalDateTime.now());
            // Copy other modifiable fields from eventDTO to event here
            Event updatedEvent = eventRepo.save(event);
            log.info("Event with id: {} updated successfully", updatedEvent.getEvent_id());
            return eventMapper.toDto(updatedEvent);
        }
        return null;
    }


    public void deleteEventById (Integer id){
        eventRepo.deleteById(id);
    }
}
