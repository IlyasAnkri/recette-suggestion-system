package com.recipeadjuster.shared.event;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserPreferenceUpdatedEvent extends BaseEvent {
    private Payload payload;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payload {
        private String userId;
        private List<String> dietaryRestrictions;
        private String cuisinePreference;
        private Integer maxCookTime;
    }

    public UserPreferenceUpdatedEvent(String source, Payload payload) {
        super("user.preference.updated", source);
        this.payload = payload;
    }
}
