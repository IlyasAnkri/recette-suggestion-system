package com.recipeadjuster.analytics.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private Integer dailyActiveUsers;
    private Integer totalSearchesToday;
    private Integer totalSubstitutionsToday;
    private BigDecimal avgIngredientsPerSearch;
    private Integer totalRecipeViews;
}
