package com.recipeadjuster.analytics.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopularRecipeResponse {
    private String recipeId;
    private String recipeName;
    private Integer viewCount;
    private Integer rank;
}
