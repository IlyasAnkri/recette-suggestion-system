package com.recipeadjuster.analytics.repository;

import com.recipeadjuster.analytics.model.entity.DailySearches;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailySearchesRepository extends JpaRepository<DailySearches, LocalDate> {
    List<DailySearches> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
