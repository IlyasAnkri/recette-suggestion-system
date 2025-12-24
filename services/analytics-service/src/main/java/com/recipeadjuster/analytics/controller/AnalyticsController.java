package com.recipeadjuster.analytics.controller;

import com.recipeadjuster.analytics.model.dto.DashboardResponse;
import com.recipeadjuster.analytics.model.dto.PopularRecipeResponse;
import com.recipeadjuster.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardResponse> getDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        DashboardResponse dashboard = analyticsService.getDashboard(startDate, endDate);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/recipes/popular")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PopularRecipeResponse>> getPopularRecipes() {
        List<PopularRecipeResponse> popularRecipes = analyticsService.getPopularRecipes();
        return ResponseEntity.ok(popularRecipes);
    }
}
