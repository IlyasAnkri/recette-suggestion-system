package com.recipeadjuster.analytics.repository;

import com.recipeadjuster.analytics.model.entity.DailyActiveUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyActiveUsersRepository extends JpaRepository<DailyActiveUsers, LocalDate> {
    List<DailyActiveUsers> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
