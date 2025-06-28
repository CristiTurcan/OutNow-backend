package com.example.outnowbackend.event.service;

import com.example.outnowbackend.businessaccount.domain.BusinessAccount;
import com.example.outnowbackend.businessaccount.repository.BusinessAccountRepo;
import com.example.outnowbackend.config.PersonalizationProperties;
import com.example.outnowbackend.event.ScoredEvent;
import com.example.outnowbackend.event.domain.Event;
import com.example.outnowbackend.event.dto.EventDTO;
import com.example.outnowbackend.event.mapper.EventMapper;
import com.example.outnowbackend.event.repository.EventAttendanceRepo;
import com.example.outnowbackend.event.repository.EventRepo;
import com.example.outnowbackend.notification.domain.NotificationType;
import com.example.outnowbackend.notification.service.DeviceTokenService;
import com.example.outnowbackend.notification.service.NotificationService;
import com.example.outnowbackend.notification.service.PushNotificationService;
import com.example.outnowbackend.user.domain.User;
import com.example.outnowbackend.user.repository.UserRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);
    private static final int LOG_TOP_N = 20;
    private final EventRepo eventRepo;
    private final BusinessAccountRepo businessAccountRepo;
    private final EventMapper eventMapper;
    private final UserRepo userRepo;
    private final DeviceTokenService tokens;
    private final PushNotificationService push;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final PersonalizationProperties personalizationProperties;
    @PersistenceContext
    private final EntityManager em;
    private final EventAttendanceRepo attendanceRepo;

    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6_371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Transactional(readOnly = true)
    public long getAttendanceCount(Integer eventId) {
        return attendanceRepo.countByEventId(eventId);
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
        if (!existing.getEndDate().equals(updatedEvent.getEndDate())) {
            changes.append("End Date: ").append(updatedEvent.getEndDate()).append("\n");
        }
        if (!existing.getEndTime().equals(updatedEvent.getEndTime())) {
            changes.append("End Time: ").append(updatedEvent.getEndTime()).append("\n");
        }
        if (!existing.getTotalTickets().equals(updatedEvent.getTotalTickets())) {
            changes.append("Total Tickets: ").append(updatedEvent.getTotalTickets()).append("\n");
        }

        // remove newline if present
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
        existing.setEndDate(updatedEvent.getEndDate());
        existing.setEndTime(updatedEvent.getEndTime());
        existing.setTotalTickets(updatedEvent.getTotalTickets());
        existing.setLatitude(updatedEvent.getLatitude());
        existing.setLongitude(updatedEvent.getLongitude());
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

        userRepo.deleteGoingByEventId(eventId);
        attendanceRepo.deleteByEventId(eventId);

        eventRepo.deleteById(eventId);
        messagingTemplate.convertAndSend("/topic/eventDeleted", eventId);
    }

    @Transactional(readOnly = true)
    public List<EventDTO> getPersonalizedForUser(Integer userId, boolean isBusiness) {
        System.out.println("getPersonalizedForUser\n");

        System.out.println("userId: " + userId + "business:" + isBusiness);

        BusinessAccount businessAccount = isBusiness ? businessAccountRepo.findById(userId).orElseThrow(() -> new EntityNotFoundException("Business Account not found: " + userId)) : null;
        User user = !isBusiness ? userRepo.findById(userId).orElseThrow(() -> new EntityNotFoundException("User Account not found: " + userId)) : null;

        List<ScoredEvent> scored = eventRepo
                .findUpcoming(LocalDate.now(), LocalTime.now())
                .stream()
                .map(eventMapper::toDTO)
                .map(dto -> computeFeatures(dto,
                        isBusiness
                                ? businessAccount.getInterestList()
                                : user.getInterestList(),
                        isBusiness
                                ? businessAccount.getLatitude()
                                : user.getLatitude(),
                        isBusiness
                                ? businessAccount.getLongitude()
                                : user.getLongitude()
                ))
                .collect(Collectors.toList());

        if (log.isDebugEnabled()) {
            for (ScoredEvent se : scored.stream().limit(LOG_TOP_N).toList()) {
                // recompute daysUntil
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime eventAt = LocalDateTime.of(
                        LocalDate.parse(se.getEvent().getEventDate()),
                        LocalTime.parse(se.getEvent().getEventTime())
                );
                double daysUntil = ChronoUnit.MINUTES.between(now, eventAt) / 60.0 / 24.0;
                // recompute timeScore
                double decayAlpha = personalizationProperties.getDecayAlpha();
                double timeScore = daysUntil <= 0
                        ? 0.0
                        : Math.exp(-decayAlpha * daysUntil);
                // recompute locationScore
                Double dtoLat = se.getEvent().getLatitude();
                Double dtoLng = se.getEvent().getLongitude();
                double locScore = (dtoLat != null && dtoLng != null)
                        ? Math.exp(-personalizationProperties.getProximityDecayAlpha()
                        * haversine(!isBusiness ? user.getLatitude() : businessAccount.getLatitude(), !isBusiness ? user.getLongitude() : businessAccount.getLongitude(),
                        dtoLat, dtoLng))
                        : 0.0;
                log.debug(
                        "DEBUG[{}] EID={}  daysUntil={}  timeScore={}  locationScore={}",
                        LOG_TOP_N,
                        se.getEvent().getEventId(),
                        String.format("%.3f", daysUntil),
                        String.format("%.3f", timeScore),
                        String.format("%.3f", locScore)
                );
            }
        }

        normalizePopularity(scored);
        scored.forEach(this::computeRawScore);
        double mmrLambda = personalizationProperties.getMmrLambda();
        List<ScoredEvent> topN = scored.stream()
                .sorted(Comparator.comparingDouble(ScoredEvent::getRawScore).reversed())
                .limit(LOG_TOP_N)
                .collect(Collectors.toList());
        long perfect = topN.stream()
                .filter(s -> s.getInterestScore() == 1.0)
                .count();
        double fraction = (double) perfect / LOG_TOP_N;
        if (fraction >= personalizationProperties.getDynamicMmrTriggerFraction()) {
            mmrLambda += personalizationProperties.getDynamicMmrBoost();
        }
        List<ScoredEvent> diversified = diversify(scored, mmrLambda);

        // ─── DEBUG LOG the top N component scores ───
        diversified.stream()
                .limit(LOG_TOP_N)
                .forEach(se -> {
                    double eventLat = se.getEvent().getLatitude();
                    double eventLng = se.getEvent().getLongitude();
                    double userLat = !isBusiness ? user.getLatitude() : businessAccount.getLatitude();
                    double userLng = !isBusiness ? user.getLongitude() : businessAccount.getLongitude();
                    double distanceKm = haversine(userLat, userLng, eventLat, eventLng);

                    log.debug(
                            "UID={}  EID={}  E_TITLE={} coords={{  dist={}km  scores={{interest:{}, time:{}, pop:{}, rating:{}, location:{}, raw:{}}}",
                            userId,
                            se.getEvent().getEventId(),
                            se.getEvent().getTitle(),
//                            userLat, userLng,
//                            eventLat, eventLng,
                            String.format("%.2f", distanceKm),
                            String.format("%.3f", se.interestScore),
                            String.format("%.3f", se.timeScore),
                            String.format("%.3f", se.popScore),
                            String.format("%.3f", se.ratingScore),
                            String.format("%.3f", se.locationScore),
                            String.format("%.3f", se.rawScore)
                    );
                });


        return diversify(scored, mmrLambda).stream()
                .map(ScoredEvent::getEvent)
                .collect(Collectors.toList());
    }

    private ScoredEvent computeFeatures(EventDTO dto, String userInterests, double userLat, double userLng) {
        ScoredEvent se = new ScoredEvent(dto);

        // interest match
        se.interestScore = jaccard(userInterests, dto.getInterestList());

        // time decay
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime eventAt = LocalDateTime.of(
                LocalDate.parse(dto.getEventDate()),
                LocalTime.parse(dto.getEventTime())
        );
        double daysUntil = ChronoUnit.MINUTES.between(now, eventAt) / 60.0 / 24.0;
        se.timeScore = daysUntil <= 0
                ? 0.0
                : Math.exp(-personalizationProperties.getDecayAlpha() * daysUntil);

        // raw popularity & rating
        se.popRaw = Math.sqrt(dto.getFavoriteCount() + dto.getAttendanceCount());
        se.ratingScore = dto.getAverageRating();
        Double dtoLat = dto.getLatitude();
        Double dtoLng = dto.getLongitude();
        if (dtoLat != null && dtoLng != null) {
            double distanceKm = haversine(userLat, userLng, dtoLat, dtoLng);
            se.locationScore = Math.exp(-personalizationProperties.getLocation() * distanceKm);
        } else {
            se.locationScore = 0.0;
        }
        return se;
    }

    private void normalizePopularity(List<ScoredEvent> list) {
        list.forEach(s -> {
            double raw = s.popRaw;
            s.popScore = raw / (1 + raw);
        });
    }

    private void computeRawScore(ScoredEvent s) {
        var w = personalizationProperties.getWeights();
        s.rawScore = w.getInterest() * s.interestScore
                + w.getTime() * s.timeScore
                + w.getPopularity() * s.popScore
                + w.getRating() * s.ratingScore
                + personalizationProperties.getLocation() * s.locationScore;
    }

    private List<ScoredEvent> diversify(List<ScoredEvent> scored, double mmrLambda) {
        List<ScoredEvent> R = scored.stream()
                .sorted(Comparator.comparingDouble((ScoredEvent s) -> s.rawScore).reversed())
                .limit(50)
                .collect(Collectors.toList());

        List<ScoredEvent> chosen = new ArrayList<>();
        if (!R.isEmpty()) chosen.add(R.remove(0));
        while (!R.isEmpty()) {
            ScoredEvent next = R.stream()
                    .max(Comparator.comparingDouble(s ->
                            mmrLambda * s.rawScore
                                    - (1 - mmrLambda) * maxSim(s, chosen)
                    ))
                    .get();
            chosen.add(next);
            R.remove(next);
        }

        List<ScoredEvent> rest = scored.stream()
                .sorted(Comparator.comparingDouble((ScoredEvent s) -> s.rawScore).reversed())
                .skip(R.size())
                .collect(Collectors.toList());

        return Stream.concat(chosen.stream(), rest.stream())
                .collect(Collectors.toList());
    }

    private double maxSim(ScoredEvent s, List<ScoredEvent> chosen) {
        return chosen.stream()
                .mapToDouble(c -> jaccard(s.getEvent().getInterestList(), c.getEvent().getInterestList()))
                .max().orElse(0);
    }

    private double jaccard(String a, String b) {
        if (a == null || b == null || a.isBlank() || b.isBlank()) {
            return 0.0;
        }
        Set<String> setA = Arrays.stream(a.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
        Set<String> setB = Arrays.stream(b.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
        if (setA.isEmpty() || setB.isEmpty()) {
            return 0.0;
        }
        long inter = setA.stream().filter(setB::contains).count();
        long union = setA.size() + setB.size() - inter;
        return (double) inter / union;
    }


}
