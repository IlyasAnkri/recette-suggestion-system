package com.recipeadjuster.shared.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipeadjuster.shared.model.entity.Ingredient;
import com.recipeadjuster.shared.model.entity.Recipe;
import com.recipeadjuster.shared.repository.IngredientRepository;
import com.recipeadjuster.shared.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataSeedService implements CommandLineRunner {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting data seeding process...");
        
        seedIngredients();
        seedRecipes();
        
        log.info("Data seeding completed successfully");
    }

    private void seedIngredients() throws IOException {
        long existingCount = ingredientRepository.count();
        if (existingCount > 0) {
            log.info("Ingredients already seeded (count: {}), skipping...", existingCount);
            return;
        }

        log.info("Seeding ingredients...");
        ClassPathResource resource = new ClassPathResource("seed-data/ingredients.json");
        List<Ingredient> ingredients = objectMapper.readValue(
            resource.getInputStream(),
            new TypeReference<List<Ingredient>>() {}
        );

        ingredientRepository.saveAll(ingredients);
        log.info("Successfully seeded {} ingredients", ingredients.size());
    }

    private void seedRecipes() throws IOException {
        long existingCount = recipeRepository.count();
        if (existingCount > 0) {
            log.info("Recipes already seeded (count: {}), skipping...", existingCount);
            return;
        }

        log.info("Seeding recipes...");
        ClassPathResource resource = new ClassPathResource("seed-data/recipes.json");
        List<Recipe> recipes = objectMapper.readValue(
            resource.getInputStream(),
            new TypeReference<List<Recipe>>() {}
        );

        LocalDateTime now = LocalDateTime.now();
        for (Recipe recipe : recipes) {
            recipe.setCreatedAt(now);
            recipe.setUpdatedAt(now);
            recipe.setRatings(Recipe.Ratings.builder()
                .average(4.5)
                .count(10)
                .build());
            recipe.setAuthor(Recipe.Author.builder()
                .userId(UUID.randomUUID())
                .name("System")
                .build());
        }

        recipeRepository.saveAll(recipes);
        log.info("Successfully seeded {} recipes", recipes.size());
    }
}
