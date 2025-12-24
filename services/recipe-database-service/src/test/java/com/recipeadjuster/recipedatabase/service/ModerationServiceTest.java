package com.recipeadjuster.recipedatabase.service;

import com.recipeadjuster.recipedatabase.exception.NotFoundException;
import com.recipeadjuster.recipedatabase.model.Recipe;
import com.recipeadjuster.recipedatabase.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModerationServiceTest {
    
    @Mock
    private RecipeRepository recipeRepository;
    
    @Mock
    private KafkaProducerService kafkaProducerService;
    
    private ModerationService moderationService;
    
    @BeforeEach
    void setUp() {
        moderationService = new ModerationService(recipeRepository, kafkaProducerService);
    }
    
    @Test
    void getPendingRecipes_ReturnsUnapprovedRecipes() {
        Recipe recipe1 = createRecipe("Recipe 1", false);
        Recipe recipe2 = createRecipe("Recipe 2", false);
        
        when(recipeRepository.findByIsApproved(false))
            .thenReturn(Arrays.asList(recipe1, recipe2));
        
        List<Recipe> pending = moderationService.getPendingRecipes();
        
        assertEquals(2, pending.size());
        assertFalse(pending.get(0).getIsApproved());
        assertFalse(pending.get(1).getIsApproved());
        verify(recipeRepository).findByIsApproved(false);
    }
    
    @Test
    void approveRecipe_SetsApprovedTrue() {
        Recipe recipe = createRecipe("Test Recipe", false);
        recipe.setId("recipe123");
        
        when(recipeRepository.findById("recipe123")).thenReturn(Optional.of(recipe));
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(i -> i.getArgument(0));
        
        Recipe approved = moderationService.approveRecipe("recipe123");
        
        assertTrue(approved.getIsApproved());
        assertNotNull(approved.getApprovedAt());
        verify(recipeRepository).save(recipe);
    }
    
    @Test
    void approveRecipe_NotFound_ThrowsException() {
        when(recipeRepository.findById("nonexistent")).thenReturn(Optional.empty());
        
        assertThrows(NotFoundException.class, () -> {
            moderationService.approveRecipe("nonexistent");
        });
    }
    
    @Test
    void rejectRecipe_DeletesRecipe() {
        Recipe recipe = createRecipe("Bad Recipe", false);
        recipe.setId("recipe456");
        
        when(recipeRepository.findById("recipe456")).thenReturn(Optional.of(recipe));
        
        moderationService.rejectRecipe("recipe456", "Inappropriate content");
        
        verify(recipeRepository).deleteById("recipe456");
    }
    
    @Test
    void rejectRecipe_NotFound_ThrowsException() {
        when(recipeRepository.findById("nonexistent")).thenReturn(Optional.empty());
        
        assertThrows(NotFoundException.class, () -> {
            moderationService.rejectRecipe("nonexistent", "Test reason");
        });
    }
    
    private Recipe createRecipe(String title, boolean isApproved) {
        Recipe recipe = new Recipe();
        recipe.setTitle(title);
        recipe.setDescription("Test description");
        recipe.setIngredients(Arrays.asList(
            new Recipe.RecipeIngredient("1", "ingredient1", 1.0, "cup"),
            new Recipe.RecipeIngredient("2", "ingredient2", 2.0, "tbsp")
        ));
        recipe.setInstructions(Arrays.asList("Step 1", "Step 2"));
        recipe.setAuthorId(UUID.randomUUID());
        recipe.setAuthorName("Test Author");
        recipe.setIsApproved(isApproved);
        recipe.setCreatedAt(LocalDateTime.now());
        recipe.setUpdatedAt(LocalDateTime.now());
        return recipe;
    }
}
