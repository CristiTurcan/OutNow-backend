package com.example.outnowbackend.feedback.controller;

import com.example.outnowbackend.feedback.dto.FeedbackDTO;
import com.example.outnowbackend.feedback.dto.RatingAndCommentRequest;
import com.example.outnowbackend.feedback.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/event/{eventId}/user/{userId}")
    public ResponseEntity<FeedbackDTO> addFeedback(
            @PathVariable Integer eventId,
            @PathVariable Integer userId,
            @RequestBody RatingAndCommentRequest request) {

        FeedbackDTO createdFeedback = feedbackService.addFeedback(
                eventId,
                userId,
                request.getRating(),
                request.getComment()
        );
        return ResponseEntity.ok(createdFeedback);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<FeedbackDTO>> getFeedbackForEvent(@PathVariable Integer eventId) {
        List<FeedbackDTO> feedbackList = feedbackService.getFeedbackForEvent(eventId);
        return ResponseEntity.ok(feedbackList);
    }
}
