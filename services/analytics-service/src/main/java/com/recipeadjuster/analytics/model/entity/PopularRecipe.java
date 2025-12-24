package com.recipeadjuster.analytics.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "popular_recipes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopularRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String recipeId;

    @Column(nullable = false)
    private String recipeName;

    @Column(nullable = false)
    private Integer viewCount;

    @Column(nullable = false)
    private LocalDate aggregationDate;

    @Column
    private Integer rank;
}
