package com.recipeadjuster.analytics.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "substitution_stats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubstitutionStats {

    @Id
    private LocalDate date;

    @Column(nullable = false)
    private Integer count;

    @Column(name = "avg_substitutions_per_request")
    private Integer avgSubstitutionsPerRequest;
}
