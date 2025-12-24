package com.recipeadjuster.ingredient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipeadjuster.ingredient.dto.IngredientMatchRequest;
import com.recipeadjuster.shared.model.entity.Recipe;
import com.recipeadjuster.shared.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class IngredientMatchControllerTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7");

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RecipeRepository recipeRepository;

    @BeforeEach
    void setUp() {
        recipeRepository.deleteAll();
        seedTestRecipes();
    }

    @Test
    void shouldMatchRecipesByIngredients() throws Exception {
        IngredientMatchRequest request = IngredientMatchRequest.builder()
            .ingredients(Arrays.asList("chicken", "garlic", "olive oil"))
            .minMatchPercentage(60)
            .build();

        mockMvc.perform(post("/api/v1/ingredients/match")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.matches").isArray())
            .andExpect(jsonPath("$.totalResults").isNumber());
    }

    @Test
    void shouldFilterByCuisine() throws Exception {
        IngredientMatchRequest request = IngredientMatchRequest.builder()
            .ingredients(Arrays.asList("tomato", "cheese"))
            .filters(IngredientMatchRequest.FilterOptions.builder()
                .cuisines(Arrays.asList("Italian"))
                .build())
            .build();

        mockMvc.perform(post("/api/v1/ingredients/match")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.matches[*].cuisine").value("Italian"));
    }

    @Test
    void shouldReturnBadRequestForEmptyIngredients() throws Exception {
        IngredientMatchRequest request = IngredientMatchRequest.builder()
            .ingredients(Arrays.asList())
            .build();

        mockMvc.perform(post("/api/v1/ingredients/match")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFilterByDietaryRestrictions() throws Exception {
        IngredientMatchRequest request = IngredientMatchRequest.builder()
            .ingredients(Arrays.asList("tomato", "cheese"))
            .filters(IngredientMatchRequest.FilterOptions.builder()
                .dietary(Arrays.asList("vegetarian"))
                .build())
            .build();

        mockMvc.perform(post("/api/v1/ingredients/match")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.matches").isArray());
    }

    @Test
    void shouldFilterByMultipleDietaryRestrictions() throws Exception {
        IngredientMatchRequest request = IngredientMatchRequest.builder()
            .ingredients(Arrays.asList("tomato", "basil"))
            .filters(IngredientMatchRequest.FilterOptions.builder()
                .dietary(Arrays.asList("vegetarian", "vegan"))
                .build())
            .build();

        mockMvc.perform(post("/api/v1/ingredients/match")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.matches").isArray());
    }

    private void seedTestRecipes() {
        Recipe recipe1 = Recipe.builder()
            .title("Chicken Pasta")
            .slug("chicken-pasta")
            .description("Simple chicken pasta")
            .ingredients(Arrays.asList(
                Recipe.RecipeIngredient.builder().name("chicken").optional(false).build(),
                Recipe.RecipeIngredient.builder().name("pasta").optional(false).build(),
                Recipe.RecipeIngredient.builder().name("garlic").optional(false).build()
            ))
            .instructions(Arrays.asList(
                Recipe.Instruction.builder().stepNumber(1).description("Cook").build()
            ))
            .prepTime(10)
            .cookTime(20)
            .servings(4)
            .difficulty(Recipe.Difficulty.EASY)
            .cuisine("Italian")
            .isApproved(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .ratings(Recipe.Ratings.builder().average(4.5).count(10).build())
            .build();

        recipeRepository.save(recipe1);
    }
}
