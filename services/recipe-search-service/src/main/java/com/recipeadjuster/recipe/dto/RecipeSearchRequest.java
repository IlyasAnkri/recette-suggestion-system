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
public class RecipeSearchRequest {
    private String query;
    private List<String> cuisines;
    private List<String> dietary;
    private String difficulty;
    private Integer maxPrepTime;
    private Integer maxCookTime;
    private Integer page = 1;
    private Integer limit = 20;
    private String sort = "rating";
}
