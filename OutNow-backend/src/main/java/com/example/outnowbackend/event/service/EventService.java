package com.example.outnowbackend.event.service;

import com.example.outnowbackend.businessaccount.domain.BusinessAccount;
import com.example.outnowbackend.businessaccount.repository.BusinessAccountRepo;
import com.example.outnowbackend.event.domain.Event;
import com.example.outnowbackend.event.mapper.EventMapper;
import com.example.outnowbackend.event.repository.EventRepo;
import com.example.outnowbackend.event.dto.EventDTO;
import com.example.outnowbackend.user.repository.UserRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepo eventRepo;
    private final BusinessAccountRepo businessAccountRepo;
    private final EventMapper eventMapper;
    private final UserRepo userRepo;

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public long getAttendanceCount(Integer eventId) {
        return userRepo.countAttendeesByEventId(eventId);
    }

    @Transactional(readOnly = true)
    public long getFavoriteCount(Integer eventId) {
        return userRepo.countByFavoritedEvents_EventId(eventId);
    }

    @Transactional(readOnly = true)
    public long getUniqueFavoriteCount(Integer eventId) {
        String sql =
                "SELECT COUNT(*) " +
                        "  FROM user_favorited_events uf " +
                        "  LEFT JOIN event_attendance ea " +
                        "    ON uf.user_id = ea.user_id " +
                        "   AND uf.event_id = ea.event_id " +
                        " WHERE uf.event_id = :eventId " +
                        "   AND ea.id IS NULL";

        Object raw = em
                .createNativeQuery(sql)
                .setParameter("eventId", eventId)
                .getSingleResult();

        Number number = (Number) raw;
        return number.longValue();
    }


    // Create a new event associated with a business account
    @Transactional
    public EventDTO createEvent(Event event, Integer businessAccountId) {
        Optional<BusinessAccount> account = businessAccountRepo.findById(businessAccountId);
        if (account.isEmpty()) {
            throw new RuntimeException("Business account not found");
        }
        event.setBusinessAccount(account.get());
        Event createdEvent = eventRepo.save(event);
        return eventMapper.toDTO(createdEvent);
    }

    // Retrieve all events
    public List<EventDTO> getAllEvents() {
        return eventRepo.findAll().stream()
                .map(eventMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<EventDTO> getEventById(Integer eventId) {
        return eventRepo.findById(eventId)
                .map(eventMapper::toDTO);
    }


    @Transactional
    public List<EventDTO> getEventsByBusinessAccount(Integer businessAccountId) {
        return eventRepo.findByBusinessAccount_Id(businessAccountId)
                .stream()
                .map(eventMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Update existing event details
    @Transactional
    public EventDTO updateEvent(Integer eventId, Event updatedEvent) {

        Event event = eventRepo.findById(eventId).map(existing -> {
            existing.setTitle(updatedEvent.getTitle());
            existing.setDescription(updatedEvent.getDescription());
            existing.setImageUrl(updatedEvent.getImageUrl());
            existing.setLocation(updatedEvent.getLocation());
            existing.setPrice(updatedEvent.getPrice());
            existing.setEventDate(updatedEvent.getEventDate());
            existing.setEventTime(updatedEvent.getEventTime());
            existing.setInterestList(updatedEvent.getInterestList());

            return eventRepo.save(existing);
        }).orElseThrow(() -> new RuntimeException("Event not found"));
        return eventMapper.toDTO(event);
    }

    public List<EventDTO> searchEvents(String q) {
        // Compute once, so these are effectively final
        final Double maxPrice = (q != null && q.matches("^\\d+(\\.\\d+)?$"))
                ? Double.parseDouble(q)
                : null;
        final String text = (maxPrice == null) ? q : null;

        SearchSession search = Search.session(em);
        List<Event> hits = search.search(Event.class)
                .where(f -> f.bool(b -> {
                    // text clause
                    if (text != null && !text.isBlank()) {
                        b.must(f.bool(inner -> {
                            inner.should(f.match()
                                    .fields("title", "location",
                                            "businessAccount.username", "interestList")
                                    .matching(text)
                                    .fuzzy(2));
                            inner.should(f.prefix()
                                    .fields("title", "location",
                                            "businessAccount.username", "interestList")
                                    .matching(text.toLowerCase()));
                        }));
                    }
                    // numeric clause
                    if (maxPrice != null) {
                        b.must(f.range()
                                .field("price")
                                .atMost(maxPrice));
                    }
                }))
                .sort(f -> {
                    if (maxPrice != null) {
                        return f.field("price").asc();
                    }
                    return f.score();
                })
                .fetchHits(50);

        return hits.stream()
                .map(eventMapper::toDTO)
                .toList();
    }


    // Delete event by id
    @Transactional
    public void deleteEvent(Integer eventId) {
        eventRepo.deleteById(eventId);
    }
}
