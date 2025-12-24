package com.recipeadjuster.analytics.service;

import com.recipeadjuster.analytics.model.dto.DashboardResponse;
import com.recipeadjuster.analytics.model.dto.PopularRecipeResponse;
import com.recipeadjuster.analytics.model.entity.*;
import com.recipeadjuster.analytics.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final DailyActiveUsersRepository dailyActiveUsersRepository;
    private final DailySearchesRepository dailySearchesRepository;
    private final PopularRecipeRepository popularRecipeRepository;
    private final SubstitutionStatsRepository substitutionStatsRepository;

    public DashboardResponse getDashboard(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();
        
        Integer dau = dailyActiveUsersRepository.findById(today)
            .map(DailyActiveUsers::getCount)
            .orElse(0);
        
        Integer searches = dailySearchesRepository.findById(today)
            .map(DailySearches::getCount)
            .orElse(0);
        
        Integer substitutions = substitutionStatsRepository.findById(today)
            .map(SubstitutionStats::getCount)
            .orElse(0);
        
        BigDecimal avgIngredients = dailySearchesRepository.findById(today)
            .map(DailySearches::getAvgIngredientsPerSearch)
            .orElse(BigDecimal.ZERO);
        
        List<PopularRecipe> popularRecipes = popularRecipeRepository.findLatestTopRecipes();
        Integer totalViews = popularRecipes.stream()
            .mapToInt(PopularRecipe::getViewCount)
            .sum();
        
        return DashboardResponse.builder()
            .dailyActiveUsers(dau)
            .totalSearchesToday(searches)
            .totalSubstitutionsToday(substitutions)
            .avgIngredientsPerSearch(avgIngredients)
            .totalRecipeViews(totalViews)
            .build();
    }

    public List<PopularRecipeResponse> getPopularRecipes() {
        return popularRecipeRepository.findLatestTopRecipes().stream()
            .limit(10)
            .map(recipe -> PopularRecipeResponse.builder()
                .recipeId(recipe.getRecipeId())
                .recipeName(recipe.getRecipeName())
                .viewCount(recipe.getViewCount())
                .rank(recipe.getRank())
                .build())
            .collect(Collectors.toList());
    }
}
