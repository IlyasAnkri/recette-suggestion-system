package com.recipeadjuster.ingredient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientMatchResponse {
    private List<RecipeMatch> matches;
    private Integer totalResults;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecipeMatch {
        private String recipeId;
        private String title;
        private Double matchPercentage;
        private List<String> matchedIngredients;
        private List<String> missingIngredients;
        private String thumbnail;
        private String cuisine;
        private Integer cookTime;
        private String difficulty;
    }
}
