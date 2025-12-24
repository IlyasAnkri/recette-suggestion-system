package com.recipeadjuster.ingredient.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class IngredientMatchRequest {
    
    @NotEmpty(message = "Ingredients list cannot be empty")
    private List<String> ingredients;
    
    @Min(value = 0, message = "minMatchPercentage must be between 0 and 100")
    @Max(value = 100, message = "minMatchPercentage must be between 0 and 100")
    private Integer minMatchPercentage = 50;
    
    @Min(value = 1, message = "maxResults must be at least 1")
    @Max(value = 100, message = "maxResults cannot exceed 100")
    private Integer maxResults = 20;
    
    private FilterOptions filters;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FilterOptions {
        private List<String> cuisines;
        private Integer maxCookTime;
        private List<String> difficulty;
        private List<String> dietary;
    }
}
