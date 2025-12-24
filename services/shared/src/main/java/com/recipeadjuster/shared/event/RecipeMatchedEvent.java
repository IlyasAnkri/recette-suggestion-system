package com.recipeadjuster.shared.event;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RecipeMatchedEvent extends BaseEvent {
    private Payload payload;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payload {
        private String userId;
        private String sessionId;
        private String recipeId;
        private String recipeName;
        private Integer matchPercentage;
        private List<String> matchedIngredients;
    }

    public RecipeMatchedEvent(String source, Payload payload) {
        super("recipe.matched", source);
        this.payload = payload;
    }
}
