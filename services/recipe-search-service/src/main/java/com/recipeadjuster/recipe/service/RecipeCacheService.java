package com.recipeadjuster.recipe.service;

import com.recipeadjuster.shared.model.entity.Recipe;
import com.recipeadjuster.shared.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeCacheService {

    private final RecipeRepository recipeRepository;

    @Cacheable(value = "popularRecipes", key = "#id")
    public Recipe getRecipeById(String id) {
        log.debug("Cache miss for recipe ID: {}", id);
        return recipeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Recipe not found: " + id));
    }
}
