package com.recipeadjuster.recipedatabase.service;

import com.recipeadjuster.recipedatabase.event.RecipeEvent;
import com.recipeadjuster.recipedatabase.exception.NotFoundException;
import com.recipeadjuster.recipedatabase.model.Recipe;
import com.recipeadjuster.recipedatabase.repository.RecipeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ModerationService {
    
    private static final Logger log = LoggerFactory.getLogger(ModerationService.class);
    private final RecipeRepository recipeRepository;
    private final KafkaProducerService kafkaProducerService;
    
    public ModerationService(RecipeRepository recipeRepository, KafkaProducerService kafkaProducerService) {
        this.recipeRepository = recipeRepository;
        this.kafkaProducerService = kafkaProducerService;
    }
    
    public List<Recipe> getPendingRecipes() {
        log.info("Fetching pending recipes for moderation");
        return recipeRepository.findByIsApproved(false);
    }
    
    public Recipe approveRecipe(String id) {
        log.info("Approving recipe: {}", id);
        
        Recipe recipe = recipeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Recipe not found: " + id));
        
        recipe.setIsApproved(true);
        recipe.setApprovedAt(LocalDateTime.now());
        
        Recipe approved = recipeRepository.save(recipe);
        log.info("Recipe approved: {}", approved.getId());
        
        // Publish Kafka event recipe.created
        RecipeEvent event = new RecipeEvent(
            "recipe.created",
            approved.getId(),
            approved.getTitle(),
            approved.getAuthorId(),
            approved.getAuthorName(),
            approved.getCuisine(),
            approved.getDifficulty(),
            UUID.randomUUID().toString()
        );
        kafkaProducerService.publishRecipeCreated(event);
        
        // TODO: Send approval email to author
        
        return approved;
    }
    
    public void rejectRecipe(String id, String reason) {
        log.info("Rejecting recipe: {} with reason: {}", id, reason);
        
        Recipe recipe = recipeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Recipe not found: " + id));
        
        // TODO: Send rejection email with feedback
        log.info("Rejection reason: {}", reason);
        
        // Soft delete - keep for audit trail
        recipeRepository.deleteById(id);
        log.info("Recipe rejected and deleted: {}", id);
    }
}
