package com.recipeadjuster.userprofile.model.dto;

import com.recipeadjuster.userprofile.model.entity.UserPreferences;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePreferencesRequest {
    private List<UserPreferences.DietaryRestriction> dietaryRestrictions;
    private List<String> allergies;
    private List<String> cuisinePreferences;
    private UserPreferences.SkillLevel skillLevel;
    private Integer householdSize;
    private UserPreferences.MeasurementSystem measurementSystem;
    private Integer defaultServings;
}
