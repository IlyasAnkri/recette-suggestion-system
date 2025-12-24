package com.recipeadjuster.substitution.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubstitutionResponseDto {
    
    private List<IngredientSubstitutions> substitutions;
    private Double confidence;
    private Boolean aiGenerated;
    private Long responseTimeMs;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IngredientSubstitutions {
        private String original;
        private List<SubstitutionOption> suggestions;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubstitutionOption {
        private String substitute;
        private String conversionRatio;
        private Double compatibilityScore;
        private String explanation;
        private String flavorImpact;
        private String textureImpact;
        private List<String> dietaryInfo;
        private String category;
        private String costTier;
        private NutritionalComparison nutritionalComparison;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NutritionalComparison {
        private String calories;
        private String protein;
        private String fat;
        private String carbs;
    }
}
