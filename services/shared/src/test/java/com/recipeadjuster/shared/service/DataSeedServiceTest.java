package com.recipeadjuster.shared.service;

import com.recipeadjuster.shared.model.entity.Ingredient;
import com.recipeadjuster.shared.model.entity.Recipe;
import com.recipeadjuster.shared.repository.IngredientRepository;
import com.recipeadjuster.shared.repository.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Testcontainers
@Import(DataSeedService.class)
class DataSeedServiceTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Test
    void shouldSeedIngredientsSuccessfully() {
        long count = ingredientRepository.count();
        assertThat(count).isGreaterThan(0);
    }

    @Test
    void shouldSeedRecipesSuccessfully() {
        long count = recipeRepository.count();
        assertThat(count).isGreaterThan(0);
    }

    @Test
    void shouldFindRecipeBySlug() {
        Optional<Recipe> recipe = recipeRepository.findBySlug("classic-spaghetti-carbonara");
        assertThat(recipe).isPresent();
        assertThat(recipe.get().getTitle()).isEqualTo("Classic Spaghetti Carbonara");
    }

    @Test
    void shouldFindIngredientByName() {
        Optional<Ingredient> ingredient = ingredientRepository.findByName("chicken breast");
        assertThat(ingredient).isPresent();
        assertThat(ingredient.get().getCategory()).isEqualTo(Ingredient.IngredientCategory.PROTEIN);
    }

    @Test
    void recipesShouldHaveRequiredFields() {
        Recipe recipe = recipeRepository.findBySlug("classic-spaghetti-carbonara").orElseThrow();
        
        assertThat(recipe.getTitle()).isNotNull();
        assertThat(recipe.getSlug()).isNotNull();
        assertThat(recipe.getIngredients()).isNotEmpty();
        assertThat(recipe.getInstructions()).isNotEmpty();
        assertThat(recipe.getPrepTime()).isGreaterThan(0);
        assertThat(recipe.getCookTime()).isGreaterThan(0);
        assertThat(recipe.getServings()).isGreaterThan(0);
        assertThat(recipe.getDifficulty()).isNotNull();
        assertThat(recipe.getCuisine()).isNotNull();
        assertThat(recipe.getIsApproved()).isTrue();
    }

    @Test
    void ingredientsShouldHaveAliases() {
        Ingredient ingredient = ingredientRepository.findByName("spinach").orElseThrow();
        
        assertThat(ingredient.getAliases()).isNotEmpty();
        assertThat(ingredient.getAliases()).contains("greens");
    }
}
