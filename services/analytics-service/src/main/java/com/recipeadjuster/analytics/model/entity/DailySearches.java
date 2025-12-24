package com.recipeadjuster.analytics.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "daily_searches")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailySearches {

    @Id
    private LocalDate date;

    @Column(nullable = false)
    private Integer count;

    @Column(name = "avg_ingredients_per_search", precision = 10, scale = 2)
    private BigDecimal avgIngredientsPerSearch;
}
