package com.recipeadjuster.recipedatabase.service;

import com.recipeadjuster.recipedatabase.dto.CreateRecipeRequest;
import com.recipeadjuster.recipedatabase.dto.ValidationErrorResponse;
import com.recipeadjuster.recipedatabase.exception.DuplicateRecipeException;
import com.recipeadjuster.recipedatabase.model.Recipe;
import com.recipeadjuster.recipedatabase.repository.IngredientRepository;
import com.recipeadjuster.recipedatabase.repository.RecipeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class RecipeValidationService {
    
    private static final Logger log = LoggerFactory.getLogger(RecipeValidationService.class);
    private static final Pattern HTTPS_URL_PATTERN = Pattern.compile("^https://.*\\.(jpg|jpeg|png|webp)$", Pattern.CASE_INSENSITIVE);
    private static final int MAX_TITLE_LENGTH = 200;
    private static final int MAX_DESCRIPTION_LENGTH = 2000;
    private static final int MIN_INGREDIENTS = 2;
    private static final int MIN_INSTRUCTIONS = 1;
    private static final int MAX_PREP_TIME = 1440; // 24 hours in minutes
    private static final int MAX_COOK_TIME = 1440;
    
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    
    public RecipeValidationService(RecipeRepository recipeRepository, IngredientRepository ingredientRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
    }
    
    public ValidationErrorResponse validateRecipe(CreateRecipeRequest request) {
        List<ValidationErrorResponse.ValidationError> errors = new ArrayList<>();
        
        // Task 1: Schema validation
        validateRequiredFields(request, errors);
        validateFieldLengths(request, errors);
        
        // Task 2: Ingredient ID verification
        validateIngredientIds(request, errors);
        
        // Task 4: Image validation
        validateImageUrls(request, errors);
        
        // Task 5: Time validation
        validateTimes(request, errors);
        
        // Task 3: Duplicate detection (throws exception for 409)
        checkForDuplicates(request);
        
        if (errors.isEmpty()) {
            return null;
        }
        
        return new ValidationErrorResponse(400, errors);
    }
    
    private void validateRequiredFields(CreateRecipeRequest request, List<ValidationErrorResponse.ValidationError> errors) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            errors.add(new ValidationErrorResponse.ValidationError("title", "Title is required"));
        }
        
        if (request.getIngredients() == null || request.getIngredients().isEmpty()) {
            errors.add(new ValidationErrorResponse.ValidationError("ingredients", "At least " + MIN_INGREDIENTS + " ingredients required"));
        } else if (request.getIngredients().size() < MIN_INGREDIENTS) {
            errors.add(new ValidationErrorResponse.ValidationError("ingredients", "At least " + MIN_INGREDIENTS + " ingredients required"));
        }
        
        if (request.getInstructions() == null || request.getInstructions().isEmpty()) {
            errors.add(new ValidationErrorResponse.ValidationError("instructions", "At least " + MIN_INSTRUCTIONS + " instruction required"));
        }
    }
    
    private void validateFieldLengths(CreateRecipeRequest request, List<ValidationErrorResponse.ValidationError> errors) {
        if (request.getTitle() != null && request.getTitle().length() > MAX_TITLE_LENGTH) {
            errors.add(new ValidationErrorResponse.ValidationError("title", "Title must not exceed " + MAX_TITLE_LENGTH + " characters"));
        }
        
        if (request.getDescription() != null && request.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            errors.add(new ValidationErrorResponse.ValidationError("description", "Description must not exceed " + MAX_DESCRIPTION_LENGTH + " characters"));
        }
    }
    
    private void validateImageUrls(CreateRecipeRequest request, List<ValidationErrorResponse.ValidationError> errors) {
        if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
            if (!HTTPS_URL_PATTERN.matcher(request.getImageUrl()).matches()) {
                errors.add(new ValidationErrorResponse.ValidationError("imageUrl", "Image URL must be HTTPS and have valid format (jpg, jpeg, png, webp)"));
            }
        }
        
        if (request.getThumbnailUrl() != null && !request.getThumbnailUrl().isEmpty()) {
            if (!HTTPS_URL_PATTERN.matcher(request.getThumbnailUrl()).matches()) {
                errors.add(new ValidationErrorResponse.ValidationError("thumbnailUrl", "Thumbnail URL must be HTTPS and have valid format (jpg, jpeg, png, webp)"));
            }
        }
    }
    
    private void validateTimes(CreateRecipeRequest request, List<ValidationErrorResponse.ValidationError> errors) {
        if (request.getPrepTime() != null) {
            if (request.getPrepTime() <= 0) {
                errors.add(new ValidationErrorResponse.ValidationError("prepTime", "Prep time must be a positive integer"));
            } else if (request.getPrepTime() > MAX_PREP_TIME) {
                errors.add(new ValidationErrorResponse.ValidationError("prepTime", "Prep time must not exceed " + MAX_PREP_TIME + " minutes"));
            }
        }
        
        if (request.getCookTime() != null) {
            if (request.getCookTime() <= 0) {
                errors.add(new ValidationErrorResponse.ValidationError("cookTime", "Cook time must be a positive integer"));
            } else if (request.getCookTime() > MAX_COOK_TIME) {
                errors.add(new ValidationErrorResponse.ValidationError("cookTime", "Cook time must not exceed " + MAX_COOK_TIME + " minutes"));
            }
        }
    }
    
    private void checkForDuplicates(CreateRecipeRequest request) {
        if (request.getTitle() == null) {
            return;
        }
        
        List<Recipe> existingRecipes = recipeRepository.findAll();
        
        for (Recipe existing : existingRecipes) {
            // Fuzzy match on title (Levenshtein distance < 3)
            int distance = levenshteinDistance(
                request.getTitle().toLowerCase().trim(),
                existing.getTitle().toLowerCase().trim()
            );
            
            if (distance < 3) {
                // Check ingredient overlap
                double overlap = calculateIngredientOverlap(request, existing);
                if (overlap > 0.8) {
                    throw new DuplicateRecipeException(
                        "Duplicate recipe detected: similar to existing recipe '" + existing.getTitle() + "'",
                        existing.getTitle()
                    );
                }
            }
        }
    }
    
    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        
        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                );
            }
        }
        
        return dp[s1.length()][s2.length()];
    }
    
    private double calculateIngredientOverlap(CreateRecipeRequest request, Recipe existing) {
        if (request.getIngredients() == null || existing.getIngredients() == null) {
            return 0.0;
        }
        
        Set<String> requestIngredients = new HashSet<>();
        for (Recipe.RecipeIngredient ing : request.getIngredients()) {
            if (ing.getName() != null) {
                requestIngredients.add(ing.getName().toLowerCase().trim());
            }
        }
        
        Set<String> existingIngredients = new HashSet<>();
        for (Recipe.RecipeIngredient ing : existing.getIngredients()) {
            if (ing.getName() != null) {
                existingIngredients.add(ing.getName().toLowerCase().trim());
            }
        }
        
        if (requestIngredients.isEmpty() || existingIngredients.isEmpty()) {
            return 0.0;
        }
        
        Set<String> intersection = new HashSet<>(requestIngredients);
        intersection.retainAll(existingIngredients);
        
        Set<String> union = new HashSet<>(requestIngredients);
        union.addAll(existingIngredients);
        
        return (double) intersection.size() / union.size();
    }
    
    private void validateIngredientIds(CreateRecipeRequest request, List<ValidationErrorResponse.ValidationError> errors) {
        // Task 2: Ingredient verification against ingredient collection
        if (request.getIngredients() != null) {
            for (int i = 0; i < request.getIngredients().size(); i++) {
                Recipe.RecipeIngredient ingredient = request.getIngredients().get(i);
                String ingredientId = ingredient.getIngredientId();
                
                if (ingredientId != null && !ingredientId.isEmpty()) {
                    // Verify ingredient ID exists in ingredient collection
                    if (!ingredientRepository.existsById(ingredientId)) {
                        errors.add(new ValidationErrorResponse.ValidationError(
                            "ingredients[" + i + "].ingredientId",
                            "Ingredient ID '" + ingredientId + "' not found in ingredient database"
                        ));
                    }
                }
            }
        }
    }
}
