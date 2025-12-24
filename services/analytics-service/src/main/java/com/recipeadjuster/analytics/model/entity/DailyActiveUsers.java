package com.recipeadjuster.analytics.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "daily_active_users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyActiveUsers {

    @Id
    private LocalDate date;

    @Column(nullable = false)
    private Integer count;
}
