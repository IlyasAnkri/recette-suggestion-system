package com.recipeadjuster.shared.event;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SubstitutionCompletedEvent extends BaseEvent {
    private Payload payload;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payload {
        private String userId;
        private String recipeId;
        private String originalIngredient;
        private List<String> suggestedSubstitutions;
        private Integer substitutionCount;
    }

    public SubstitutionCompletedEvent(String source, Payload payload) {
        super("substitution.completed", source);
        this.payload = payload;
    }
}
