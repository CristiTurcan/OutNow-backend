package com.example.outnowbackend.service;

import com.example.outnowbackend.entity.Event;
import com.example.outnowbackend.repository.EventRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventRepo eventRepo;

    public List<Event> getAllEvents(){
        return eventRepo.findAll();
    }

    public Event getEventById(Integer id){
        Optional<Event> optionalEvent = eventRepo.findById(id);
        if(optionalEvent.isPresent()){
            return optionalEvent.get();
        }
        log.info("Event with id: {} doesn't exist", id);
        return null;
    }

    public Event saveEvent (Event event){
//        event.setCreatedAt(LocalDateTime.now());
//        event.setUpdatedAt(LocalDateTime.now());
//        Event savedEvent = eventRepo.save(event);
//
//        log.info("Event with id: {} saved successfully", event.getEvent_id());
//        return savedEvent;
        return null;
    }

    public Event updateEvent (Event event) {
//        Optional<Event> existingEvent = eventRepo.findById(event.getEvent_id());
//        event.setCreatedAt(existingEvent.get().getCreatedAt());
//        event.setUpdatedAt(LocalDateTime.now());
//
//        Event updatedEvent = eventRepo.save(event);
//
//        log.info("Event with id: {} updated successfully", event.getEvent_id());
//        return updatedEvent;
        return null;
    }

    public void deleteEventById (Integer id){
        eventRepo.deleteById(id);
    }
}
