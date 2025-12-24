package com.recipeadjuster.analytics.service;

import com.recipeadjuster.analytics.model.entity.*;
import com.recipeadjuster.analytics.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregationService {

    private final EventTrackingRepository eventTrackingRepository;
    private final DailyActiveUsersRepository dailyActiveUsersRepository;
    private final DailySearchesRepository dailySearchesRepository;
    private final PopularRecipeRepository popularRecipeRepository;
    private final SubstitutionStatsRepository substitutionStatsRepository;

    @Scheduled(cron = "0 0 0 * * *", zone = "UTC")
    @Transactional
    public void runDailyAggregation() {
        LocalDate yesterday = LocalDate.now(ZoneOffset.UTC).minusDays(1);
        log.info("Starting daily aggregation for date: {}", yesterday);
        
        aggregateDailyActiveUsers(yesterday);
        aggregateDailySearches(yesterday);
        aggregatePopularRecipes(yesterday);
        aggregateSubstitutionStats(yesterday);
        
        log.info("Completed daily aggregation for date: {}", yesterday);
    }

    private void aggregateDailyActiveUsers(LocalDate date) {
        Instant start = date.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = start.plus(1, ChronoUnit.DAYS);
        
        Long count = eventTrackingRepository.countDistinctUsersByTimestampBetween(start, end);
        
        DailyActiveUsers dau = DailyActiveUsers.builder()
            .date(date)
            .count(count.intValue())
            .build();
        
        dailyActiveUsersRepository.save(dau);
        log.debug("Aggregated DAU for {}: {} users", date, count);
    }

    private void aggregateDailySearches(LocalDate date) {
        Instant start = date.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = start.plus(1, ChronoUnit.DAYS);
        
        List<EventTracking> searches = eventTrackingRepository
            .findByEventTypeAndTimestampBetween("ingredient.submitted", start, end);
        
        int totalSearches = searches.size();
        double avgIngredients = searches.stream()
            .mapToInt(e -> {
                String metadata = e.getMetadata();
                return metadata.split("ingredients").length - 1;
            })
            .average()
            .orElse(0.0);
        
        DailySearches dailySearches = DailySearches.builder()
            .date(date)
            .count(totalSearches)
            .avgIngredientsPerSearch(BigDecimal.valueOf(avgIngredients).setScale(2, RoundingMode.HALF_UP))
            .build();
        
        dailySearchesRepository.save(dailySearches);
        log.debug("Aggregated searches for {}: {} searches", date, totalSearches);
    }

    private void aggregatePopularRecipes(LocalDate date) {
        Instant start = date.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = start.plus(1, ChronoUnit.DAYS);
        
        List<Object[]> topRecipes = eventTrackingRepository.findTopRecipesByViews(start, end);
        
        int rank = 1;
        for (Object[] result : topRecipes) {
            if (rank > 10) break;
            
            String recipeId = (String) result[0];
            Long viewCount = (Long) result[1];
            
            PopularRecipe popularRecipe = PopularRecipe.builder()
                .recipeId(recipeId)
                .recipeName("Recipe " + recipeId)
                .viewCount(viewCount.intValue())
                .aggregationDate(date)
                .rank(rank++)
                .build();
            
            popularRecipeRepository.save(popularRecipe);
        }
        
        log.debug("Aggregated popular recipes for {}: {} recipes", date, topRecipes.size());
    }

    private void aggregateSubstitutionStats(LocalDate date) {
        Instant start = date.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = start.plus(1, ChronoUnit.DAYS);
        
        List<EventTracking> substitutions = eventTrackingRepository
            .findByEventTypeAndTimestampBetween("substitution.completed", start, end);
        
        int totalSubstitutions = substitutions.size();
        int avgPerRequest = totalSubstitutions > 0 ? totalSubstitutions / substitutions.size() : 0;
        
        SubstitutionStats stats = SubstitutionStats.builder()
            .date(date)
            .count(totalSubstitutions)
            .avgSubstitutionsPerRequest(avgPerRequest)
            .build();
        
        substitutionStatsRepository.save(stats);
        log.debug("Aggregated substitution stats for {}: {} substitutions", date, totalSubstitutions);
    }
}
