package com.recipeadjuster.analytics.service;

import com.recipeadjuster.analytics.model.entity.*;
import com.recipeadjuster.analytics.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AggregationServiceTest {

    @Mock
    private EventTrackingRepository eventTrackingRepository;
    
    @Mock
    private DailyActiveUsersRepository dailyActiveUsersRepository;
    
    @Mock
    private DailySearchesRepository dailySearchesRepository;
    
    @Mock
    private PopularRecipeRepository popularRecipeRepository;
    
    @Mock
    private SubstitutionStatsRepository substitutionStatsRepository;

    @InjectMocks
    private AggregationService aggregationService;

    @Test
    void shouldRunDailyAggregation() {
        LocalDate yesterday = LocalDate.now(ZoneOffset.UTC).minusDays(1);
        Instant start = yesterday.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = start.plusSeconds(86400);

        when(eventTrackingRepository.countDistinctUsersByTimestampBetween(any(), any()))
            .thenReturn(100L);
        
        when(eventTrackingRepository.findByEventTypeAndTimestampBetween(
            eq("ingredient.submitted"), any(), any()))
            .thenReturn(List.of());
        
        when(eventTrackingRepository.findByEventTypeAndTimestampBetween(
            eq("substitution.completed"), any(), any()))
            .thenReturn(List.of());
        
        when(eventTrackingRepository.findTopRecipesByViews(any(), any()))
            .thenReturn(List.of());

        aggregationService.runDailyAggregation();

        verify(dailyActiveUsersRepository, times(1)).save(any(DailyActiveUsers.class));
        verify(dailySearchesRepository, times(1)).save(any(DailySearches.class));
        verify(substitutionStatsRepository, times(1)).save(any(SubstitutionStats.class));
    }
}
