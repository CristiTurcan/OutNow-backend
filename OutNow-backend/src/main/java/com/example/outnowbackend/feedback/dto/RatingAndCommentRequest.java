package com.example.outnowbackend.feedback.dto;

import lombok.Data;

@Data
public class RatingAndCommentRequest {
    private String rating;
    private String comment;
}
