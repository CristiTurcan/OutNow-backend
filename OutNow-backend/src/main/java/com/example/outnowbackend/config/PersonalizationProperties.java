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

    /**
     * Weight for location proximity (0.0 to 1.0)
     */
    private double location;
    /**
     * Amount to boost MMR lambda when many top events are perfect interest matches
     */
    private double dynamicMmrBoost;
    /**
     * Fraction threshold of top-K raw events with perfect interest match to trigger boost
     */
    private double dynamicMmrTriggerFraction;
    private double proximityDecayAlpha;

    @Data
    public static class Weights {
        private double interest;
        private double time;
        private double popularity;
        private double rating;
    }
}
