package com.recipeadjuster.recipedatabase.service;

import com.recipeadjuster.recipedatabase.exception.NotFoundException;
import com.recipeadjuster.recipedatabase.exception.UnauthorizedException;
import com.recipeadjuster.recipedatabase.exception.ValidationException;
import com.recipeadjuster.recipedatabase.model.Rating;
import com.recipeadjuster.recipedatabase.model.Recipe;
import com.recipeadjuster.recipedatabase.repository.RatingRepository;
import com.recipeadjuster.recipedatabase.repository.RecipeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RatingService {
    
    private static final Logger log = LoggerFactory.getLogger(RatingService.class);
    private final RatingRepository ratingRepository;
    private final RecipeRepository recipeRepository;
    
    public RatingService(RatingRepository ratingRepository, RecipeRepository recipeRepository) {
        this.ratingRepository = ratingRepository;
        this.recipeRepository = recipeRepository;
    }
    
    public Rating rateRecipe(String recipeId, UUID userId, Integer ratingValue) {
        log.info("User {} rating recipe {} with {} stars", userId, recipeId, ratingValue);
        
        if (ratingValue < 1 || ratingValue > 5) {
            throw new ValidationException("Rating must be between 1 and 5");
        }
        
        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new NotFoundException("Recipe not found: " + recipeId));
        
        if (recipe.getAuthorId().equals(userId)) {
            throw new UnauthorizedException("You cannot rate your own recipe");
        }
        
        Optional<Rating> existingRating = ratingRepository.findByUserIdAndRecipeId(userId, recipeId);
        
        Rating rating;
        if (existingRating.isPresent()) {
            rating = existingRating.get();
            rating.setRating(ratingValue);
            rating.setUpdatedAt(LocalDateTime.now());
            log.info("Updating existing rating");
        } else {
            rating = new Rating(userId, recipeId, ratingValue);
            log.info("Creating new rating");
        }
        
        Rating saved = ratingRepository.save(rating);
        recalculateRecipeRating(recipeId);
        
        return saved;
    }
    
    private void recalculateRecipeRating(String recipeId) {
        List<Rating> ratings = ratingRepository.findByRecipeId(recipeId);
        
        if (ratings.isEmpty()) {
            return;
        }
        
        double average = ratings.stream()
            .mapToInt(Rating::getRating)
            .average()
            .orElse(0.0);
        
        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new NotFoundException("Recipe not found: " + recipeId));
        
        Recipe.Ratings recipeRatings = new Recipe.Ratings(average, ratings.size());
        recipe.setRatings(recipeRatings);
        recipeRepository.save(recipe);
        
        log.info("Recipe {} rating updated: {} stars ({} ratings)", recipeId, 
                 String.format("%.2f", average), ratings.size());
    }
}
