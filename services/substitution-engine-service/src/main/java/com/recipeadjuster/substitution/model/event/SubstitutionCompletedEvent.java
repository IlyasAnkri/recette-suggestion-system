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
public class SubstitutionCompletedEvent {
    
    private String eventId;
    @Builder.Default
    private String eventType = "substitution.completed";
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
        private List<SubstitutionResult> substitutions;
        private Long responseTimeMs;
        private Boolean cacheHit;
        private Boolean aiGenerated;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubstitutionResult {
        private String original;
        private String suggested;
        private String source;
    }
}
