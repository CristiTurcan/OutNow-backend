package com.example.outnowbackend.notification.controller;

import com.example.outnowbackend.notification.scheduler.EventNotificationScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/notifications")
@RequiredArgsConstructor
public class NotificationTestController {

    private final EventNotificationScheduler scheduler;

    /** Trigger the 24h & 1h reminders immediately */
    @PostMapping("/reminders")
    public ResponseEntity<Void> triggerReminders() {
        scheduler.reminderJobs();
        return ResponseEntity.ok().build();
    }

    /** Trigger the ~1h-after feedback invites immediately */
    @PostMapping("/feedback-invites")
    public ResponseEntity<Void> triggerFeedbackInvites() {
        scheduler.feedbackInvites();
        return ResponseEntity.ok().build();
    }
}
