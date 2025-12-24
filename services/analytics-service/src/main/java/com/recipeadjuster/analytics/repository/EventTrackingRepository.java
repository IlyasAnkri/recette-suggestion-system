package com.recipeadjuster.analytics.repository;

import com.recipeadjuster.analytics.model.entity.EventTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface EventTrackingRepository extends JpaRepository<EventTracking, Long> {
    
    List<EventTracking> findByEventTypeAndTimestampBetween(
        String eventType, Instant start, Instant end);
    
    @Query("SELECT COUNT(DISTINCT e.userId) FROM EventTracking e " +
           "WHERE e.timestamp >= :start AND e.timestamp < :end")
    Long countDistinctUsersByTimestampBetween(Instant start, Instant end);
    
    @Query("SELECT e.recipeId, COUNT(e) as viewCount FROM EventTracking e " +
           "WHERE e.eventType = 'recipe.matched' " +
           "AND e.timestamp >= :start AND e.timestamp < :end " +
           "GROUP BY e.recipeId ORDER BY viewCount DESC")
    List<Object[]> findTopRecipesByViews(Instant start, Instant end);
}
