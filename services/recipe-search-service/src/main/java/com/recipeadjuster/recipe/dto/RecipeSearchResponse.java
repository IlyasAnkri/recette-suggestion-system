package com.recipeadjuster.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeSearchResponse {
    private List<RecipeSummary> recipes;
    private PaginationMetadata pagination;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecipeSummary {
        private String id;
        private String title;
        private String description;
        private String thumbnail;
        private Integer prepTime;
        private Integer cookTime;
        private String difficulty;
        private Double rating;
        private Integer ratingCount;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationMetadata {
        private Integer page;
        private Integer limit;
        private Long total;
        private Integer totalPages;
    }
}
