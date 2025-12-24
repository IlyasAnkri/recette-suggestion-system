package com.recipeadjuster.recipedatabase.controller;

import com.recipeadjuster.recipedatabase.dto.CreateRecipeRequest;
import com.recipeadjuster.recipedatabase.dto.RateRecipeRequest;
import com.recipeadjuster.recipedatabase.dto.UpdateRecipeRequest;
import com.recipeadjuster.recipedatabase.dto.ValidationErrorResponse;
import com.recipeadjuster.recipedatabase.exception.DuplicateRecipeException;
import com.recipeadjuster.recipedatabase.model.Rating;
import com.recipeadjuster.recipedatabase.model.Recipe;
import com.recipeadjuster.recipedatabase.service.RatingService;
import com.recipeadjuster.recipedatabase.service.RecipeService;
import com.recipeadjuster.recipedatabase.service.RecipeValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/recipes")
public class RecipeController {
    
    private static final Logger log = LoggerFactory.getLogger(RecipeController.class);
    private final RecipeService recipeService;
    private final RatingService ratingService;
    private final RecipeValidationService validationService;
    
    public RecipeController(RecipeService recipeService, RatingService ratingService, RecipeValidationService validationService) {
        this.recipeService = recipeService;
        this.ratingService = ratingService;
        this.validationService = validationService;
    }
    
    @PostMapping
    public ResponseEntity<?> createRecipe(
            @RequestBody CreateRecipeRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestHeader(value = "X-User-Name", required = false, defaultValue = "Anonymous") String userName) {
        
        log.info("POST /api/v1/recipes - Creating recipe: {}", request.getTitle());
        
        // Validate recipe
        ValidationErrorResponse validationErrors = validationService.validateRecipe(request);
        if (validationErrors != null) {
            log.warn("Recipe validation failed: {} errors", validationErrors.getErrors().size());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationErrors);
        }
        
        // Extract user ID from JWT (simulated via header for now)
        UUID authorId = userIdHeader != null ? UUID.fromString(userIdHeader) : UUID.randomUUID();
        
        try {
            Recipe created = recipeService.createRecipe(request, authorId, userName);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (DuplicateRecipeException e) {
            log.warn("Duplicate recipe detected: {}", e.getMessage());
            ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                409,
                Collections.singletonList(
                    new ValidationErrorResponse.ValidationError("duplicate", e.getMessage())
                )
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipe(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        
        log.info("GET /api/v1/recipes/{} - Fetching recipe", id);
        
        UUID requestingUserId = userIdHeader != null ? UUID.fromString(userIdHeader) : null;
        
        Recipe recipe = recipeService.getRecipeById(id, requestingUserId);
        return ResponseEntity.ok(recipe);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(
            @PathVariable String id,
            @RequestBody UpdateRecipeRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestHeader(value = "X-User-Role", required = false, defaultValue = "USER") String userRole) {
        
        log.info("PUT /api/v1/recipes/{} - Updating recipe", id);
        
        UUID requestingUserId = userIdHeader != null ? UUID.fromString(userIdHeader) : UUID.randomUUID();
        boolean isModerator = "MODERATOR".equals(userRole) || "ADMIN".equals(userRole);
        
        Recipe updated = recipeService.updateRecipe(id, request, requestingUserId, isModerator);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestHeader(value = "X-User-Role", required = false, defaultValue = "USER") String userRole) {
        
        log.info("DELETE /api/v1/recipes/{} - Deleting recipe", id);
        
        UUID requestingUserId = userIdHeader != null ? UUID.fromString(userIdHeader) : UUID.randomUUID();
        boolean isAdmin = "ADMIN".equals(userRole);
        
        recipeService.deleteRecipe(id, requestingUserId, isAdmin);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/rate")
    public ResponseEntity<Rating> rateRecipe(
            @PathVariable String id,
            @RequestBody RateRecipeRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        
        log.info("POST /api/v1/recipes/{}/rate - Rating recipe", id);
        
        UUID userId = userIdHeader != null ? UUID.fromString(userIdHeader) : UUID.randomUUID();
        Rating rating = ratingService.rateRecipe(id, userId, request.getRating());
        
        return ResponseEntity.ok(rating);
    }
}
