package com.example.outnowbackend.feedback.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FeedbackDTO {
    private Long feedbackId;
    private Integer eventId;
    private Integer userId;
    private String rating; // e.g.: "VERY_BAD", "BAD", etc.
    private String comment;
    private LocalDateTime createdAt;
}
