package com.recipeadjuster.recipedatabase.dto;

import com.recipeadjuster.recipedatabase.model.Recipe.RecipeIngredient;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRecipeRequest {
    private String title;
    private String description;
    private List<RecipeIngredient> ingredients;
    private List<String> instructions;
    private String imageUrl;
    private String thumbnailUrl;
    private Integer prepTime;
    private Integer cookTime;
    private Integer servings;
    private String cuisine;
    private String difficulty;
}
