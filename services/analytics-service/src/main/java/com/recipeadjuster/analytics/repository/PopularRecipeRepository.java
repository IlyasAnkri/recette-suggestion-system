package com.recipeadjuster.analytics.repository;

import com.recipeadjuster.analytics.model.entity.PopularRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PopularRecipeRepository extends JpaRepository<PopularRecipe, Long> {
    List<PopularRecipe> findByAggregationDateOrderByRankAsc(LocalDate date);
    
    @Query("SELECT p FROM PopularRecipe p WHERE p.aggregationDate = " +
           "(SELECT MAX(pr.aggregationDate) FROM PopularRecipe pr) " +
           "ORDER BY p.rank ASC")
    List<PopularRecipe> findLatestTopRecipes();
}
