package com.recipeadjuster.substitution.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubstitutionRequestDto {
    
    private String recipeId;
    
    @NotEmpty(message = "At least one missing ingredient is required")
    private List<String> missingIngredients;
    
    private List<String> availableIngredients;
    
    private PreferencesDto preferences;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PreferencesDto {
        private List<String> dietaryRestrictions;
        private Boolean preferBudget;
    }
}
