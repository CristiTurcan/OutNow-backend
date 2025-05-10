package com.example.outnowbackend.event.service;

import com.example.outnowbackend.businessaccount.domain.BusinessAccount;
import com.example.outnowbackend.businessaccount.repository.BusinessAccountRepo;
import com.example.outnowbackend.event.domain.Event;
import com.example.outnowbackend.event.dto.EventDTO;
import com.example.outnowbackend.event.mapper.EventMapper;
import com.example.outnowbackend.event.repository.EventRepo;
import com.example.outnowbackend.notification.domain.NotificationType;
import com.example.outnowbackend.notification.service.DeviceTokenService;
import com.example.outnowbackend.notification.service.NotificationService;
import com.example.outnowbackend.notification.service.PushNotificationService;
import com.example.outnowbackend.user.domain.User;
import com.example.outnowbackend.user.repository.UserRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepo eventRepo;
    private final BusinessAccountRepo businessAccountRepo;
    private final EventMapper eventMapper;
    private final UserRepo userRepo;
    private final DeviceTokenService tokens;
    private final PushNotificationService push;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

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

    @Transactional
    public EventDTO createEvent(Event event, Integer businessAccountId) {
        BusinessAccount account = businessAccountRepo.findById(businessAccountId)
                .orElseThrow(() -> new RuntimeException("Business account not found"));
        event.setBusinessAccount(account);
        Event created = eventRepo.save(event);

        // notify followers of organizer
        List<String> pushTokens = account.getFollowers()
                .stream()
                .map(User::getUserid)
                .flatMap(id -> tokens.getTokensForUser(id).stream())
                .collect(Collectors.toList());

        if (!pushTokens.isEmpty()) {
            push.sendPush(
                    pushTokens,
                    "New event from " + account.getUsername(),
                    "“" + created.getTitle() + "” is now live!",
                    Map.of("eventId", created.getEventId())
            );
        }

        // persist in-app notification for every follower
        account.getFollowers()
                .stream()
                .map(User::getUserid)
                .forEach(followerId ->
                        notificationService.createNotification(
                                followerId,
                                "New event from " + account.getUsername(),
                                "Your favorite organizer " + account.getUsername() +
                                        " just posted “" + created.getTitle() + "”", NotificationType.NEW_EVENT, event.getEventId()
                        )
                );

        EventDTO dto = eventMapper.toDTO(created);
        messagingTemplate.convertAndSend("/topic/eventCreated", dto);
        return dto;
    }

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

    @Transactional
    public EventDTO updateEvent(Integer eventId, Event updatedEvent) {
        Event existing = eventRepo.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // detect changes across every field
        StringBuilder changes = new StringBuilder();

        if (!existing.getTitle().equals(updatedEvent.getTitle())) {
            changes.append("Title: ").append(updatedEvent.getTitle()).append('\n');
        }
        if (!existing.getDescription().equals(updatedEvent.getDescription())) {
            changes.append("Description: ").append(updatedEvent.getDescription()).append("\n");
        }
//        if (!existing.getImageUrl().equals(updatedEvent.getImageUrl())) {
//            changes.append("Image: ").append(updatedEvent.getImageUrl()).append("; ");
//        }
        if (!existing.getLocation().equals(updatedEvent.getLocation())) {
            changes.append("Location: ").append(updatedEvent.getLocation()).append('\n');
        }
        if (!existing.getPrice().equals(updatedEvent.getPrice())) {
            changes.append("Price: ").append(updatedEvent.getPrice()).append("\n");
        }
        if (!existing.getEventDate().equals(updatedEvent.getEventDate())) {
            changes.append("Date: ").append(updatedEvent.getEventDate()).append("\n");
        }
        if (!existing.getEventTime().equals(updatedEvent.getEventTime())) {
            changes.append("Time: ").append(updatedEvent.getEventTime()).append("\n");
        }
        if (!existing.getInterestList().equals(updatedEvent.getInterestList())) {
            changes.append("Interests: ").append(updatedEvent.getInterestList()).append("\n");
        }

        // remove trailing newline if present
        if (!changes.isEmpty() && changes.charAt(changes.length() - 1) == '\n') {
            changes.deleteCharAt(changes.length() - 1);
        }

        // apply updates
        existing.setTitle(updatedEvent.getTitle());
        existing.setDescription(updatedEvent.getDescription());
        existing.setImageUrl(updatedEvent.getImageUrl());
        existing.setLocation(updatedEvent.getLocation());
        existing.setPrice(updatedEvent.getPrice());
        existing.setEventDate(updatedEvent.getEventDate());
        existing.setEventTime(updatedEvent.getEventTime());
        existing.setInterestList(updatedEvent.getInterestList());
        Event saved = eventRepo.save(existing);

        System.out.println("[updateEvent] called for eventId={} with changes: {}" + eventId + changes);

        // if any key fields changed, notify attendees
        if (!changes.isEmpty()) {
            List<Integer> attendeeIds = userRepo.findAttendeeIdsByEventId(eventId);
            System.out.println("Attendee count: " + attendeeIds.size());

            List<String> pushTokens = attendeeIds.stream()
                    .flatMap(id -> tokens.getTokensForUser(id).stream())
                    .collect(Collectors.toList());

            System.out.println("Attendee count: " + saved.getAttendees().size());

            if (!pushTokens.isEmpty()) {
                push.sendPush(
                        pushTokens,
                        "Event updated: " + saved.getTitle(),
                        changes.toString(),
                        Map.of("eventId", saved.getEventId())
                );
            }

            attendeeIds.forEach(uid ->
                    notificationService.createNotification(
                            uid,
                            "Event updated: " + saved.getTitle(),
                            changes.toString(), NotificationType.EVENT_UPDATED, existing.getEventId()
                    )
            );
        }

        EventDTO dto = eventMapper.toDTO(saved);
        messagingTemplate.convertAndSend("/topic/eventUpdated", dto);
        return dto;
    }

    public List<EventDTO> searchEvents(String q) {
        final Double maxPrice = (q != null && q.matches("^\\d+(\\.\\d+)?$"))
                ? Double.parseDouble(q)
                : null;
        final String text = (maxPrice == null) ? q : null;

        SearchSession search = Search.session(em);
        List<Event> hits = search.search(Event.class)
                .where(f -> f.bool(b -> {
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


    @Transactional
    public void deleteEvent(Integer eventId) {
        Event toCancel = eventRepo.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        List<Integer> attendeeIds = userRepo.findAttendeeIdsByEventId(eventId);

        String title = "Event canceled: " + toCancel.getTitle();
        String body = "We're sorry, \"" + toCancel.getTitle() + "\" has been canceled.";

        attendeeIds.forEach(uid ->
                notificationService.createNotification(uid, title, body, NotificationType.EVENT_CANCELED, toCancel.getEventId())
        );

        List<String> pushTokens = attendeeIds.stream()
                .flatMap(id -> tokens.getTokensForUser(id).stream())
                .toList();

        if (!pushTokens.isEmpty()) {
            push.sendPush(
                    pushTokens,
                    title,
                    body,
                    Map.of("eventId", toCancel.getEventId())
            );
        }

        eventRepo.deleteById(eventId);
        messagingTemplate.convertAndSend("/topic/eventDeleted", eventId);
    }
}
