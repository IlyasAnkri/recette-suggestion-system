package com.recipeadjuster.recipedatabase.service;

import com.recipeadjuster.recipedatabase.dto.CreateRecipeRequest;
import com.recipeadjuster.recipedatabase.dto.UpdateRecipeRequest;
import com.recipeadjuster.recipedatabase.event.RecipeEvent;
import com.recipeadjuster.recipedatabase.model.Recipe;
import com.recipeadjuster.recipedatabase.repository.RecipeRepository;
import com.recipeadjuster.recipedatabase.exception.NotFoundException;
import com.recipeadjuster.recipedatabase.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RecipeService {
    
    private static final Logger log = LoggerFactory.getLogger(RecipeService.class);
    private final RecipeRepository recipeRepository;
    private final KafkaProducerService kafkaProducerService;
    
    public RecipeService(RecipeRepository recipeRepository, KafkaProducerService kafkaProducerService) {
        this.recipeRepository = recipeRepository;
        this.kafkaProducerService = kafkaProducerService;
    }
    
    public Recipe createRecipe(CreateRecipeRequest request, UUID authorId, String authorName) {
        log.info("Creating recipe: {} by author: {}", request.getTitle(), authorId);
        
        Recipe recipe = new Recipe();
        recipe.setTitle(request.getTitle());
        recipe.setDescription(request.getDescription());
        recipe.setIngredients(request.getIngredients());
        recipe.setInstructions(request.getInstructions());
        recipe.setImageUrl(request.getImageUrl());
        recipe.setThumbnailUrl(request.getThumbnailUrl());
        recipe.setPrepTime(request.getPrepTime());
        recipe.setCookTime(request.getCookTime());
        recipe.setServings(request.getServings());
        recipe.setCuisine(request.getCuisine());
        recipe.setDifficulty(request.getDifficulty());
        
        recipe.setAuthorId(authorId);
        recipe.setAuthorName(authorName);
        recipe.setIsApproved(false);
        recipe.setRatings(new Recipe.Ratings(0.0, 0));
        recipe.setCreatedAt(LocalDateTime.now());
        recipe.setUpdatedAt(LocalDateTime.now());
        
        return recipeRepository.save(recipe);
    }
    
    public Recipe getRecipeById(String id, UUID requestingUserId) {
        Recipe recipe = recipeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Recipe not found: " + id));
        
        if (!recipe.getIsApproved() && !recipe.getAuthorId().equals(requestingUserId)) {
            throw new UnauthorizedException("Recipe not approved and you are not the owner");
        }
        return recipe;
    }
    
    public Recipe updateRecipe(String id, UpdateRecipeRequest request, UUID requestingUserId, boolean isModerator) {
        Recipe recipe = recipeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Recipe not found: " + id));
        
        if (!recipe.getAuthorId().equals(requestingUserId) && !isModerator) {
            throw new UnauthorizedException("You are not authorized to update this recipe");
        }
        
        if (request.getTitle() != null) recipe.setTitle(request.getTitle());
        if (request.getDescription() != null) recipe.setDescription(request.getDescription());
        if (request.getIngredients() != null) recipe.setIngredients(request.getIngredients());
        if (request.getInstructions() != null) recipe.setInstructions(request.getInstructions());
        if (request.getImageUrl() != null) recipe.setImageUrl(request.getImageUrl());
        if (request.getThumbnailUrl() != null) recipe.setThumbnailUrl(request.getThumbnailUrl());
        if (request.getPrepTime() != null) recipe.setPrepTime(request.getPrepTime());
        if (request.getCookTime() != null) recipe.setCookTime(request.getCookTime());
        if (request.getServings() != null) recipe.setServings(request.getServings());
        if (request.getCuisine() != null) recipe.setCuisine(request.getCuisine());
        if (request.getDifficulty() != null) recipe.setDifficulty(request.getDifficulty());
        
        recipe.setUpdatedAt(LocalDateTime.now());
        Recipe updated = recipeRepository.save(recipe);
        
        // Publish recipe.updated event only if recipe is approved
        if (updated.getIsApproved()) {
            RecipeEvent event = new RecipeEvent(
                "recipe.updated",
                updated.getId(),
                updated.getTitle(),
                updated.getAuthorId(),
                updated.getAuthorName(),
                updated.getCuisine(),
                updated.getDifficulty(),
                UUID.randomUUID().toString()
            );
            kafkaProducerService.publishRecipeUpdated(event);
        }
        
        return updated;
    }
    
    public void deleteRecipe(String id, UUID requestingUserId, boolean isAdmin) {
        Recipe recipe = recipeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Recipe not found: " + id));
        
        if (!recipe.getAuthorId().equals(requestingUserId) && !isAdmin) {
            throw new UnauthorizedException("You are not authorized to delete this recipe");
        }
        
        recipeRepository.deleteById(id);
    }
}
