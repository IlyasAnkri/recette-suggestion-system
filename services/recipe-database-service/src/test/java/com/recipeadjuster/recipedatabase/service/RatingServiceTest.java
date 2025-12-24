package com.recipeadjuster.recipedatabase.service;

import com.recipeadjuster.recipedatabase.exception.NotFoundException;
import com.recipeadjuster.recipedatabase.exception.UnauthorizedException;
import com.recipeadjuster.recipedatabase.exception.ValidationException;
import com.recipeadjuster.recipedatabase.model.Rating;
import com.recipeadjuster.recipedatabase.model.Recipe;
import com.recipeadjuster.recipedatabase.repository.RatingRepository;
import com.recipeadjuster.recipedatabase.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {
    
    @Mock
    private RatingRepository ratingRepository;
    
    @Mock
    private RecipeRepository recipeRepository;
    
    private RatingService ratingService;
    
    @BeforeEach
    void setUp() {
        ratingService = new RatingService(ratingRepository, recipeRepository);
    }
    
    @Test
    void testRateRecipe_FirstTime_CreatesNewRating() {
        UUID userId = UUID.randomUUID();
        String recipeId = "recipe123";
        int ratingValue = 5;
        
        Recipe recipe = createRecipe(recipeId, UUID.randomUUID());
        Rating newRating = new Rating(userId, recipeId, ratingValue);
        
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(ratingRepository.findByUserIdAndRecipeId(userId, recipeId)).thenReturn(Optional.empty());
        when(ratingRepository.save(any(Rating.class))).thenReturn(newRating);
        // After save, recalculateRecipeRating fetches all ratings including the new one
        when(ratingRepository.findByRecipeId(recipeId)).thenReturn(Arrays.asList(newRating));
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(i -> i.getArgument(0));
        
        Rating result = ratingService.rateRecipe(recipeId, userId, ratingValue);
        
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(recipeId, result.getRecipeId());
        assertEquals(ratingValue, result.getRating());
        
        verify(ratingRepository).save(any(Rating.class));
        verify(recipeRepository, times(2)).findById(recipeId); // Once for validation, once for recalculation
        verify(recipeRepository).save(any(Recipe.class));
    }
    
    @Test
    void testRateRecipe_Update_UpdatesExistingRating() {
        UUID userId = UUID.randomUUID();
        String recipeId = "recipe123";
        int newRatingValue = 4;
        
        Recipe recipe = createRecipe(recipeId, UUID.randomUUID());
        Rating existingRating = new Rating();
        existingRating.setUserId(userId);
        existingRating.setRecipeId(recipeId);
        existingRating.setRating(3);
        
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(ratingRepository.findByUserIdAndRecipeId(userId, recipeId)).thenReturn(Optional.of(existingRating));
        when(ratingRepository.save(any(Rating.class))).thenAnswer(i -> {
            Rating r = i.getArgument(0);
            r.setRating(newRatingValue);
            return r;
        });
        // After save, recalculateRecipeRating fetches the updated rating
        when(ratingRepository.findByRecipeId(recipeId)).thenReturn(Arrays.asList(existingRating));
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(i -> i.getArgument(0));
        
        Rating result = ratingService.rateRecipe(recipeId, userId, newRatingValue);
        
        assertNotNull(result);
        assertEquals(newRatingValue, result.getRating());
        
        verify(ratingRepository).save(existingRating);
        verify(recipeRepository).save(any(Recipe.class));
    }
    
    @Test
    void testRateRecipe_SelfRating_ThrowsUnauthorizedException() {
        UUID authorId = UUID.randomUUID();
        String recipeId = "recipe123";
        
        Recipe recipe = createRecipe(recipeId, authorId);
        
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        
        assertThrows(UnauthorizedException.class, () -> {
            ratingService.rateRecipe(recipeId, authorId, 5);
        }, "Should prevent self-rating");
        
        verify(ratingRepository, never()).save(any(Rating.class));
    }
    
    @Test
    void testRateRecipe_InvalidRatingTooLow_ThrowsException() {
        UUID userId = UUID.randomUUID();
        String recipeId = "recipe123";
        
        assertThrows(ValidationException.class, () -> {
            ratingService.rateRecipe(recipeId, userId, 0);
        }, "Should reject rating below 1");
    }
    
    @Test
    void testRateRecipe_InvalidRatingTooHigh_ThrowsException() {
        UUID userId = UUID.randomUUID();
        String recipeId = "recipe123";
        
        assertThrows(ValidationException.class, () -> {
            ratingService.rateRecipe(recipeId, userId, 6);
        }, "Should reject rating above 5");
    }
    
    @Test
    void testRateRecipe_RecipeNotFound_ThrowsNotFoundException() {
        UUID userId = UUID.randomUUID();
        String recipeId = "nonexistent";
        
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());
        
        assertThrows(NotFoundException.class, () -> {
            ratingService.rateRecipe(recipeId, userId, 5);
        }, "Should throw NotFoundException for non-existent recipe");
    }
    
    @Test
    void testRateRecipe_CalculatesAverageCorrectly() {
        UUID userId = UUID.randomUUID();
        String recipeId = "recipe123";
        
        Recipe recipe = createRecipe(recipeId, UUID.randomUUID());
        
        Rating rating1 = new Rating();
        rating1.setRating(5);
        Rating rating2 = new Rating();
        rating2.setRating(3);
        Rating newRating = new Rating(userId, recipeId, 4);
        
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(ratingRepository.findByUserIdAndRecipeId(userId, recipeId)).thenReturn(Optional.empty());
        when(ratingRepository.save(any(Rating.class))).thenReturn(newRating);
        // After save, recalculateRecipeRating fetches all ratings including the new one
        when(ratingRepository.findByRecipeId(recipeId)).thenReturn(Arrays.asList(rating1, rating2, newRating));
        
        ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);
        when(recipeRepository.save(recipeCaptor.capture())).thenAnswer(i -> i.getArgument(0));
        
        ratingService.rateRecipe(recipeId, userId, 4);
        
        Recipe savedRecipe = recipeCaptor.getValue();
        assertEquals(3, savedRecipe.getRatings().getCount());
        assertEquals(4.0, savedRecipe.getRatings().getAverage(), 0.01); // (5+3+4)/3 = 4.0
    }
    
    private Recipe createRecipe(String id, UUID authorId) {
        Recipe recipe = new Recipe();
        recipe.setId(id);
        recipe.setTitle("Test Recipe");
        recipe.setAuthorId(authorId);
        recipe.setRatings(new Recipe.Ratings(0.0, 0));
        return recipe;
    }
}
