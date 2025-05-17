package com.example.outnowbackend.notification.scheduler;

import com.example.outnowbackend.event.domain.Event;
import com.example.outnowbackend.event.repository.EventRepo;
import com.example.outnowbackend.feedback.repository.FeedbackRepo;
import com.example.outnowbackend.notification.domain.NotificationType;
import com.example.outnowbackend.notification.service.DeviceTokenService;
import com.example.outnowbackend.notification.service.NotificationService;
import com.example.outnowbackend.notification.service.PushNotificationService;
import com.example.outnowbackend.user.repository.UserRepo;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EventNotificationScheduler {

    private static final Logger log = LoggerFactory.getLogger(EventNotificationScheduler.class);
    private final EventRepo eventRepo;
    private final PushNotificationService push;
    private final DeviceTokenService tokens;
    private final FeedbackRepo feedbackRepo;
    private final NotificationService notificationService;
    private final UserRepo userRepo;


    public EventNotificationScheduler(
            EventRepo eventRepo,
            PushNotificationService push,
            DeviceTokenService tokens,
            FeedbackRepo feedbackRepo,
            NotificationService notificationService,
            UserRepo userRepo
    ) {
        this.eventRepo = eventRepo;
        this.push = push;
        this.tokens = tokens;
        this.feedbackRepo = feedbackRepo;
        this.notificationService = notificationService;
        this.userRepo = userRepo;
    }

    // Run once a minute to catch 24h and 1h reminders
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void reminderJobs() {
//        log.info("reminderJobs() triggered at {}", LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now();
        scheduleReminder(now.plusHours(24), "starts in 24 hours");
        scheduleReminder(now.plusHours(1), "starts in 1 hour");
    }

    private void scheduleReminder(LocalDateTime target, String when) {
        LocalDateTime truncated = target.truncatedTo(ChronoUnit.MINUTES);
        LocalDate date = truncated.toLocalDate();
        LocalTime time = truncated.toLocalTime();

//        log.info("scheduleReminder(target={}, when='{}')", target, when);
        List<Event> events = eventRepo
                .findByEventDateAndEventTime(date, time);
//        log.info("   found {} event(s) for {} reminder", events.size(), when);
        for (Event e : events) {
            List<Integer> attendeeIds = userRepo.findAttendeeIdsByEventId(e.getEventId());

//            log.info("   notifying {} attendee(s) for eventId={}", attendeeIds.size(), e.getEventId());

            String title = "Reminder: " + e.getTitle() + " " + when;
            String body = "Your event \"" + e.getTitle() + "\" " + when + " — see you there!";

            attendeeIds.forEach(attId ->
                    notificationService.createNotification(attId, title, body, NotificationType.EVENT_REMINDER, e.getEventId())
            );

            List<String> pushTokens = attendeeIds.stream()
                    .flatMap(id -> tokens.getTokensForUser(id).stream())
                    .collect(Collectors.toList());
            if (!pushTokens.isEmpty()) {
                push.sendPush(
                        pushTokens,
                        "Reminder: " + e.getTitle() + " " + when,
                        "Your event “" + e.getTitle() + "” " + when + " — see you there!",
                        Map.of("eventId", e.getEventId())
                );
            }
        }
    }

    // Run once a minute to catch feedback invites ~1h after start
    @Scheduled(cron = "30 * * * * *")
    @Transactional
    public void feedbackInvites() {
//        log.info("feedbackInvites() triggered at {}", LocalDateTime.now());
        LocalDateTime rawTarget = LocalDateTime.now().minusHours(1);
        LocalDateTime target = rawTarget.truncatedTo(ChronoUnit.MINUTES);
//        log.info("   looking for events at exact time {}", target);

        List<Event> events = eventRepo.findByEventDateAndEventTime(
                target.toLocalDate(),
                target.toLocalTime()
        );
//        log.info("   found {} event(s) ending 1h ago", events.size());


        for (Event e : events) {
            List<Integer> attendeeIds = userRepo.findAttendeeIdsByEventId(e.getEventId());

            Set<Integer> rated = feedbackRepo.findByEvent(e).stream()
                    .map(fb -> fb.getUser().getUserid())
                    .collect(Collectors.toSet());
            attendeeIds.removeAll(rated);

//            log.info("   {} attendee(s) pending feedback for eventId={}", attendeeIds.size(), e.getEventId());


            if (attendeeIds.isEmpty()) {
                continue;
            }

            String title = "How was \"" + e.getTitle() + "\"?";
            String body = "Tell us about your experience in 2 minutes!";

            attendeeIds.forEach(uid ->
                    notificationService.createNotification(uid, title, body, NotificationType.FEEDBACK_INVITE, e.getEventId())
            );

            List<String> pushTokens = attendeeIds.stream()
                    .flatMap(id -> tokens.getTokensForUser(id).stream())
                    .toList();

            if (!pushTokens.isEmpty()) {
                push.sendPush(
                        pushTokens,
                        title,
                        body,
                        Map.of("eventId", e.getEventId())
                );
            }
        }
    }
}
