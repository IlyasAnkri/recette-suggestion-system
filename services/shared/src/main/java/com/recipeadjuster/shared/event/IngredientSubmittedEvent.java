package com.recipeadjuster.shared.event;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class IngredientSubmittedEvent extends BaseEvent {
    private Payload payload;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payload {
        private String userId;
        private String sessionId;
        private List<String> ingredients;
        private Preferences preferences;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Preferences {
        private List<String> dietaryRestrictions;
        private Integer maxCookTime;
    }

    public IngredientSubmittedEvent(String source, Payload payload) {
        super("ingredient.submitted", source);
        this.payload = payload;
    }
}
