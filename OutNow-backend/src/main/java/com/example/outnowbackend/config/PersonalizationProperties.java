package com.example.outnowbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "personalization")
@Data
public class PersonalizationProperties {
    private Weights weights = new Weights();
    private double decayAlpha;
    private double mmrLambda;

    @Data
    public static class Weights {
        private double interest;
        private double time;
        private double popularity;
        private double rating;
    }
}
