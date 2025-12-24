package com.recipeadjuster.substitution.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubstitutionRequest {
    
    @NotBlank(message = "Missing ingredient is required")
    private String missingIngredient;
    
    private String recipeContext;
    
    private List<String> availableIngredients;
    
    private List<String> dietaryRestrictions;
}
