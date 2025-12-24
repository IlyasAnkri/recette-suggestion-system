package com.recipeadjuster.analytics.repository;

import com.recipeadjuster.analytics.model.entity.SubstitutionStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SubstitutionStatsRepository extends JpaRepository<SubstitutionStats, LocalDate> {
    List<SubstitutionStats> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
