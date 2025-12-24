package com.recipeadjuster.substitution.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubstitutionRequestedEvent {
    
    private String eventId;
    @Builder.Default
    private String eventType = "substitution.requested";
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    private String correlationId;
    private Payload payload;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payload {
        private String userId;
        private String recipeId;
        private List<String> missingIngredients;
        private List<String> availableIngredients;
        private List<String> dietaryRestrictions;
    }
}
