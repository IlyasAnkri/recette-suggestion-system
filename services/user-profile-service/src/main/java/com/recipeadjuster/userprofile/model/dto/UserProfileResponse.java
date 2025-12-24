package com.recipeadjuster.userprofile.model.dto;

import com.recipeadjuster.userprofile.model.entity.UserPreferences;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private UUID id;
    private String email;
    private String displayName;
    private String avatarUrl;
    private Instant createdAt;
    private PreferencesDto preferences;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PreferencesDto {
        private List<UserPreferences.DietaryRestriction> dietaryRestrictions;
        private List<String> allergies;
        private List<String> cuisinePreferences;
        private UserPreferences.SkillLevel skillLevel;
        private Integer householdSize;
        private UserPreferences.MeasurementSystem measurementSystem;
        private Integer defaultServings;
    }
}
