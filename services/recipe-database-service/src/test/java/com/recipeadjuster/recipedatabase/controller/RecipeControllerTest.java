package com.recipeadjuster.recipedatabase.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipeadjuster.recipedatabase.config.TestConfig;
import com.recipeadjuster.recipedatabase.dto.CreateRecipeRequest;
import com.recipeadjuster.recipedatabase.dto.UpdateRecipeRequest;
import com.recipeadjuster.recipedatabase.model.Recipe;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"
})
@AutoConfigureMockMvc
@Testcontainers
@Import(TestConfig.class)
class RecipeControllerTest {
    
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7");
    
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void createRecipe_shouldReturn201() throws Exception {
        CreateRecipeRequest request = new CreateRecipeRequest();
        request.setTitle("Test Recipe");
        request.setDescription("A test recipe");
        request.setIngredients(List.of(
            new Recipe.RecipeIngredient(null, "Chicken", 500.0, "g"),
            new Recipe.RecipeIngredient(null, "Salt", 5.0, "g")
        ));
        request.setInstructions(List.of("Step 1", "Step 2"));
        request.setPrepTime(15);
        request.setCookTime(30);
        request.setServings(4);
        request.setCuisine("Italian");
        request.setDifficulty("Easy");
        
        mockMvc.perform(post("/api/v1/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-User-Id", UUID.randomUUID().toString())
                .header("X-User-Name", "Test User")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("Test Recipe"))
            .andExpect(jsonPath("$.isApproved").value(false))
            .andExpect(jsonPath("$.authorName").value("Test User"));
    }
    
    @Test
    void getRecipe_shouldReturnRecipe() throws Exception {
        // First create a recipe
        CreateRecipeRequest createRequest = new CreateRecipeRequest();
        createRequest.setTitle("Get Test Recipe");
        createRequest.setDescription("Test");
        createRequest.setIngredients(List.of(
            new Recipe.RecipeIngredient(null, "Tomato", 2.0, "pcs"),
            new Recipe.RecipeIngredient(null, "Onion", 1.0, "pcs")
        ));
        createRequest.setInstructions(List.of("Cook"));
        
        UUID userId = UUID.randomUUID();
        
        String response = mockMvc.perform(post("/api/v1/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-User-Id", userId.toString())
                .header("X-User-Name", "Test User")
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        
        Recipe created = objectMapper.readValue(response, Recipe.class);
        
        // Now get the recipe
        mockMvc.perform(get("/api/v1/recipes/" + created.getId())
                .header("X-User-Id", userId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Get Test Recipe"));
    }
    
    @Test
    void updateRecipe_byOwner_shouldSucceed() throws Exception {
        // Create recipe
        CreateRecipeRequest createRequest = new CreateRecipeRequest();
        createRequest.setTitle("Original Title");
        createRequest.setDescription("Original");
        createRequest.setIngredients(List.of(
            new Recipe.RecipeIngredient(null, "Beef", 300.0, "g"),
            new Recipe.RecipeIngredient(null, "Pepper", 2.0, "g")
        ));
        createRequest.setInstructions(List.of("Cook"));
        
        UUID userId = UUID.randomUUID();
        
        String response = mockMvc.perform(post("/api/v1/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-User-Id", userId.toString())
                .header("X-User-Name", "Owner")
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        
        Recipe created = objectMapper.readValue(response, Recipe.class);
        
        // Update recipe
        UpdateRecipeRequest updateRequest = new UpdateRecipeRequest();
        updateRequest.setTitle("Updated Title");
        
        mockMvc.perform(put("/api/v1/recipes/" + created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-User-Id", userId.toString())
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated Title"));
    }
    
    @Test
    void updateRecipe_byNonOwner_shouldReturn403() throws Exception {
        // Create recipe
        CreateRecipeRequest createRequest = new CreateRecipeRequest();
        createRequest.setTitle("Owner Recipe");
        createRequest.setDescription("Test");
        createRequest.setIngredients(List.of(
            new Recipe.RecipeIngredient(null, "Fish", 200.0, "g"),
            new Recipe.RecipeIngredient(null, "Lemon", 1.0, "pcs")
        ));
        createRequest.setInstructions(List.of("Cook"));
        
        UUID ownerId = UUID.randomUUID();
        
        String response = mockMvc.perform(post("/api/v1/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-User-Id", ownerId.toString())
                .header("X-User-Name", "Owner")
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        
        Recipe created = objectMapper.readValue(response, Recipe.class);
        
        // Try to update as different user
        UpdateRecipeRequest updateRequest = new UpdateRecipeRequest();
        updateRequest.setTitle("Hacked Title");
        
        mockMvc.perform(put("/api/v1/recipes/" + created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-User-Id", UUID.randomUUID().toString())
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isForbidden());
    }
    
    @Test
    void deleteRecipe_byAdmin_shouldSucceed() throws Exception {
        // Create recipe
        CreateRecipeRequest createRequest = new CreateRecipeRequest();
        createRequest.setTitle("To Delete");
        createRequest.setDescription("Test");
        createRequest.setIngredients(List.of(
            new Recipe.RecipeIngredient(null, "Pork", 400.0, "g"),
            new Recipe.RecipeIngredient(null, "Garlic", 3.0, "cloves")
        ));
        createRequest.setInstructions(List.of("Cook"));
        
        String response = mockMvc.perform(post("/api/v1/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-User-Id", UUID.randomUUID().toString())
                .header("X-User-Name", "User")
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        
        Recipe created = objectMapper.readValue(response, Recipe.class);
        
        // Delete as admin
        mockMvc.perform(delete("/api/v1/recipes/" + created.getId())
                .header("X-User-Id", UUID.randomUUID().toString())
                .header("X-User-Role", "ADMIN"))
            .andExpect(status().isNoContent());
    }
}
