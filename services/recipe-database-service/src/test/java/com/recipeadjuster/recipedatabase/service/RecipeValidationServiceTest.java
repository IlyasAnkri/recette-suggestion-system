package com.recipeadjuster.recipedatabase.service;

import com.recipeadjuster.recipedatabase.dto.CreateRecipeRequest;
import com.recipeadjuster.recipedatabase.dto.ValidationErrorResponse;
import com.recipeadjuster.recipedatabase.exception.DuplicateRecipeException;
import com.recipeadjuster.recipedatabase.model.Recipe;
import com.recipeadjuster.recipedatabase.repository.IngredientRepository;
import com.recipeadjuster.recipedatabase.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipeValidationServiceTest {
    
    @Mock
    private RecipeRepository recipeRepository;
    
    @Mock
    private IngredientRepository ingredientRepository;
    
    private RecipeValidationService validationService;
    
    @BeforeEach
    void setUp() {
        validationService = new RecipeValidationService(recipeRepository, ingredientRepository);
    }
    
    @Test
    void testValidRecipe_ReturnsNull() {
        CreateRecipeRequest request = createValidRequest();
        when(recipeRepository.findAll()).thenReturn(Collections.emptyList());
        when(ingredientRepository.existsById("1")).thenReturn(true);
        when(ingredientRepository.existsById("2")).thenReturn(true);
        when(ingredientRepository.existsById("3")).thenReturn(true);
        
        ValidationErrorResponse result = validationService.validateRecipe(request);
        
        assertNull(result, "Valid recipe should return null");
    }
    
    @Test
    void testMissingTitle_ReturnsError() {
        CreateRecipeRequest request = createValidRequest();
        request.setTitle(null);
        
        ValidationErrorResponse result = validationService.validateRecipe(request);
        
        assertNotNull(result);
        assertEquals(400, result.getStatus());
        assertTrue(hasError(result, "title"), "Should have title error");
    }
    
    @Test
    void testInsufficientIngredients_ReturnsError() {
        CreateRecipeRequest request = createValidRequest();
        request.setIngredients(Arrays.asList(
            new Recipe.RecipeIngredient("1", "chicken", 1.0, "lb")
        ));
        when(recipeRepository.findAll()).thenReturn(Collections.emptyList());
        when(ingredientRepository.existsById("1")).thenReturn(true);
        
        ValidationErrorResponse result = validationService.validateRecipe(request);
        
        assertNotNull(result);
        assertTrue(hasError(result, "ingredients"), "Should have ingredients error");
    }
    
    @Test
    void testInvalidImageUrl_ReturnsError() {
        CreateRecipeRequest request = createValidRequest();
        request.setImageUrl("http://example.com/image.jpg"); // HTTP instead of HTTPS
        when(recipeRepository.findAll()).thenReturn(Collections.emptyList());
        when(ingredientRepository.existsById("1")).thenReturn(true);
        when(ingredientRepository.existsById("2")).thenReturn(true);
        when(ingredientRepository.existsById("3")).thenReturn(true);
        
        ValidationErrorResponse result = validationService.validateRecipe(request);
        
        assertNotNull(result);
        assertTrue(hasError(result, "imageUrl"), "Should have imageUrl error");
    }
    
    @Test
    void testInvalidImageFormat_ReturnsError() {
        CreateRecipeRequest request = createValidRequest();
        request.setImageUrl("https://example.com/image.gif"); // GIF not allowed
        when(recipeRepository.findAll()).thenReturn(Collections.emptyList());
        when(ingredientRepository.existsById("1")).thenReturn(true);
        when(ingredientRepository.existsById("2")).thenReturn(true);
        when(ingredientRepository.existsById("3")).thenReturn(true);
        
        ValidationErrorResponse result = validationService.validateRecipe(request);
        
        assertNotNull(result);
        assertTrue(hasError(result, "imageUrl"), "Should reject GIF format");
    }
    
    @Test
    void testNegativePrepTime_ReturnsError() {
        CreateRecipeRequest request = createValidRequest();
        request.setPrepTime(-10);
        when(recipeRepository.findAll()).thenReturn(Collections.emptyList());
        when(ingredientRepository.existsById("1")).thenReturn(true);
        when(ingredientRepository.existsById("2")).thenReturn(true);
        when(ingredientRepository.existsById("3")).thenReturn(true);
        
        ValidationErrorResponse result = validationService.validateRecipe(request);
        
        assertNotNull(result);
        assertTrue(hasError(result, "prepTime"), "Should have prepTime error");
    }
    
    @Test
    void testZeroCookTime_ReturnsError() {
        CreateRecipeRequest request = createValidRequest();
        request.setCookTime(0);
        when(recipeRepository.findAll()).thenReturn(Collections.emptyList());
        when(ingredientRepository.existsById("1")).thenReturn(true);
        when(ingredientRepository.existsById("2")).thenReturn(true);
        when(ingredientRepository.existsById("3")).thenReturn(true);
        
        ValidationErrorResponse result = validationService.validateRecipe(request);
        
        assertNotNull(result);
        assertTrue(hasError(result, "cookTime"), "Should have cookTime error");
    }
    
    @Test
    void testDuplicateRecipe_ThrowsDuplicateException() {
        CreateRecipeRequest request = createValidRequest();
        request.setTitle("Chicken Pasta");
        
        Recipe existingRecipe = new Recipe();
        existingRecipe.setTitle("Chicken Pasta");
        existingRecipe.setIngredients(request.getIngredients());
        
        when(recipeRepository.findAll()).thenReturn(Arrays.asList(existingRecipe));
        when(ingredientRepository.existsById("1")).thenReturn(true);
        when(ingredientRepository.existsById("2")).thenReturn(true);
        when(ingredientRepository.existsById("3")).thenReturn(true);
        
        assertThrows(DuplicateRecipeException.class, () -> {
            validationService.validateRecipe(request);
        }, "Should throw DuplicateRecipeException for duplicate recipe");
    }
    
    @Test
    void testSimilarTitleButDifferentIngredients_NoError() {
        CreateRecipeRequest request = createValidRequest();
        request.setTitle("Chicken Pasta");
        
        Recipe existingRecipe = new Recipe();
        existingRecipe.setTitle("Chicken Pasta");
        existingRecipe.setIngredients(Arrays.asList(
            new Recipe.RecipeIngredient("99", "beef", 1.0, "lb"),
            new Recipe.RecipeIngredient("100", "rice", 2.0, "cups")
        ));
        
        when(recipeRepository.findAll()).thenReturn(Arrays.asList(existingRecipe));
        when(ingredientRepository.existsById("1")).thenReturn(true);
        when(ingredientRepository.existsById("2")).thenReturn(true);
        when(ingredientRepository.existsById("3")).thenReturn(true);
        
        ValidationErrorResponse result = validationService.validateRecipe(request);
        
        assertNull(result, "Different ingredients should not trigger duplicate");
    }
    
    @Test
    void testTitleTooLong_ReturnsError() {
        CreateRecipeRequest request = createValidRequest();
        request.setTitle("A".repeat(201));
        when(recipeRepository.findAll()).thenReturn(Collections.emptyList());
        when(ingredientRepository.existsById("1")).thenReturn(true);
        when(ingredientRepository.existsById("2")).thenReturn(true);
        when(ingredientRepository.existsById("3")).thenReturn(true);
        
        ValidationErrorResponse result = validationService.validateRecipe(request);
        
        assertNotNull(result);
        assertTrue(hasError(result, "title"), "Should have title length error");
    }
    
    @Test
    void testMultipleErrors_ReturnsAllErrors() {
        CreateRecipeRequest request = createValidRequest();
        request.setTitle(null);
        request.setPrepTime(-5);
        request.setCookTime(0);
        
        ValidationErrorResponse result = validationService.validateRecipe(request);
        
        assertNotNull(result);
        assertTrue(result.getErrors().size() >= 3, "Should have multiple errors");
    }
    
    @Test
    void testInvalidIngredientId_ReturnsError() {
        CreateRecipeRequest request = createValidRequest();
        when(recipeRepository.findAll()).thenReturn(Collections.emptyList());
        when(ingredientRepository.existsById("1")).thenReturn(false);
        when(ingredientRepository.existsById("2")).thenReturn(true);
        when(ingredientRepository.existsById("3")).thenReturn(true);
        
        ValidationErrorResponse result = validationService.validateRecipe(request);
        
        assertNotNull(result);
        assertTrue(hasError(result, "ingredients[0].ingredientId"), "Should have error for invalid ingredient ID");
    }
    
    @Test
    void testDuplicateRecipeWithHighOverlap_ThrowsException() {
        CreateRecipeRequest request = createValidRequest();
        
        Recipe existingRecipe = new Recipe();
        existingRecipe.setTitle("Delicious Chicken Pasta");
        // Same ingredients to ensure >80% overlap
        existingRecipe.setIngredients(Arrays.asList(
            new Recipe.RecipeIngredient("1", "chicken", 1.0, "lb"),
            new Recipe.RecipeIngredient("2", "pasta", 8.0, "oz"),
            new Recipe.RecipeIngredient("3", "garlic", 3.0, "cloves")
        ));
        
        when(recipeRepository.findAll()).thenReturn(Collections.singletonList(existingRecipe));
        when(ingredientRepository.existsById("1")).thenReturn(true);
        when(ingredientRepository.existsById("2")).thenReturn(true);
        when(ingredientRepository.existsById("3")).thenReturn(true);
        
        assertThrows(DuplicateRecipeException.class, () -> {
            validationService.validateRecipe(request);
        }, "Should throw DuplicateRecipeException for duplicate recipe");
    }
    
    private CreateRecipeRequest createValidRequest() {
        CreateRecipeRequest request = new CreateRecipeRequest();
        request.setTitle("Delicious Chicken Pasta");
        request.setDescription("A tasty pasta dish");
        request.setIngredients(Arrays.asList(
            new Recipe.RecipeIngredient("1", "chicken", 1.0, "lb"),
            new Recipe.RecipeIngredient("2", "pasta", 8.0, "oz"),
            new Recipe.RecipeIngredient("3", "garlic", 3.0, "cloves")
        ));
        request.setInstructions(Arrays.asList(
            "Cook pasta",
            "Cook chicken",
            "Combine"
        ));
        request.setImageUrl("https://example.com/image.jpg");
        request.setPrepTime(15);
        request.setCookTime(30);
        request.setServings(4);
        request.setCuisine("Italian");
        request.setDifficulty("Medium");
        return request;
    }
    
    private boolean hasError(ValidationErrorResponse response, String field) {
        return response.getErrors().stream()
            .anyMatch(error -> error.getField().equals(field));
    }
}
