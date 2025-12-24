package com.recipeadjuster.recipe.controller;

import com.recipeadjuster.recipe.dto.RecipeSearchRequest;
import com.recipeadjuster.recipe.dto.RecipeSearchResponse;
import com.recipeadjuster.recipe.service.RecipeSearchService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/recipes")
@RequiredArgsConstructor
public class RecipeSearchController {

    private final RecipeSearchService recipeSearchService;
    private final MeterRegistry meterRegistry;

    @GetMapping
    public ResponseEntity<RecipeSearchResponse> searchRecipes(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) List<String> cuisine,
            @RequestParam(required = false) List<String> dietary,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) Integer maxPrepTime,
            @RequestParam(required = false) Integer maxCookTime,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(defaultValue = "rating") String sort) {
        
        RecipeSearchRequest request = RecipeSearchRequest.builder()
            .query(q)
            .cuisines(cuisine)
            .dietary(dietary)
            .difficulty(difficulty)
            .maxPrepTime(maxPrepTime)
            .maxCookTime(maxCookTime)
            .page(page)
            .limit(Math.min(limit, 100))
            .sort(sort)
            .build();
        
        log.info("Searching recipes with query: {}", q);
        
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            RecipeSearchResponse response = recipeSearchService.searchRecipes(request);
            sample.stop(Timer.builder("recipe.search.duration")
                .description("Recipe search query duration")
                .tag("has_query", String.valueOf(q != null))
                .tag("has_filters", String.valueOf(cuisine != null || dietary != null || difficulty != null))
                .register(meterRegistry));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            sample.stop(Timer.builder("recipe.search.duration")
                .description("Recipe search query duration")
                .tag("has_query", String.valueOf(q != null))
                .tag("has_filters", String.valueOf(cuisine != null || dietary != null || difficulty != null))
                .tag("error", "true")
                .register(meterRegistry));
            throw e;
        }
    }
}
