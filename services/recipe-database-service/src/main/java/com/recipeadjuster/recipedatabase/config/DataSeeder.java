package com.recipeadjuster.recipedatabase.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipeadjuster.recipedatabase.model.Ingredient;
import com.recipeadjuster.recipedatabase.model.Recipe;
import com.recipeadjuster.recipedatabase.repository.IngredientRepository;
import com.recipeadjuster.recipedatabase.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) {
        log.info("Starting data seeding process...");
        
        try {
            seedIngredients();
            seedRecipes();
            log.info("Data seeding completed successfully");
        } catch (Exception e) {
            log.error("Error during data seeding", e);
        }
    }

    private void seedIngredients() {
        long count = ingredientRepository.count();
        if (count > 0) {
            log.info("Ingredients already seeded (count: {}), skipping...", count);
            return;
        }

        log.info("Seeding ingredients...");
        List<Ingredient> ingredients = List.of(
            createIngredient("Chicken", "Poultry", List.of("chicken breast", "chicken thigh", "chicken")),
            createIngredient("Beef", "Meat", List.of("beef", "ground beef", "beef sirloin", "beef chuck")),
            createIngredient("Pork", "Meat", List.of("pork", "pork shoulder", "pork chops", "bacon", "pancetta")),
            createIngredient("Salmon", "Fish", List.of("salmon", "salmon fillet")),
            createIngredient("Shrimp", "Seafood", List.of("shrimp", "prawns")),
            createIngredient("Tomato", "Vegetable", List.of("tomato", "tomatoes", "tomato sauce", "marinara")),
            createIngredient("Onion", "Vegetable", List.of("onion", "onions", "red onion", "green onion")),
            createIngredient("Garlic", "Vegetable", List.of("garlic", "garlic cloves")),
            createIngredient("Carrot", "Vegetable", List.of("carrot", "carrots")),
            createIngredient("Potato", "Vegetable", List.of("potato", "potatoes", "sweet potato")),
            createIngredient("Broccoli", "Vegetable", List.of("broccoli")),
            createIngredient("Bell Pepper", "Vegetable", List.of("bell pepper", "pepper", "red pepper")),
            createIngredient("Mushroom", "Vegetable", List.of("mushroom", "mushrooms")),
            createIngredient("Spinach", "Vegetable", List.of("spinach")),
            createIngredient("Lettuce", "Vegetable", List.of("lettuce", "romaine")),
            createIngredient("Cucumber", "Vegetable", List.of("cucumber")),
            createIngredient("Rice", "Grain", List.of("rice", "arborio rice", "rice noodles")),
            createIngredient("Pasta", "Grain", List.of("pasta", "spaghetti", "fettuccine", "linguine", "noodles")),
            createIngredient("Flour", "Grain", List.of("flour", "all-purpose flour")),
            createIngredient("Bread", "Grain", List.of("bread", "buns", "tortillas", "pita")),
            createIngredient("Cheese", "Dairy", List.of("cheese", "parmesan", "mozzarella", "cheddar", "feta")),
            createIngredient("Milk", "Dairy", List.of("milk", "cream", "heavy cream")),
            createIngredient("Butter", "Dairy", List.of("butter")),
            createIngredient("Egg", "Dairy", List.of("egg", "eggs")),
            createIngredient("Yogurt", "Dairy", List.of("yogurt")),
            createIngredient("Olive Oil", "Oil", List.of("olive oil", "oil")),
            createIngredient("Soy Sauce", "Condiment", List.of("soy sauce")),
            createIngredient("Lemon", "Fruit", List.of("lemon", "lemon juice")),
            createIngredient("Lime", "Fruit", List.of("lime", "lime juice")),
            createIngredient("Basil", "Herb", List.of("basil", "fresh basil")),
            createIngredient("Parsley", "Herb", List.of("parsley", "fresh parsley")),
            createIngredient("Cilantro", "Herb", List.of("cilantro", "coriander")),
            createIngredient("Ginger", "Spice", List.of("ginger", "fresh ginger")),
            createIngredient("Cumin", "Spice", List.of("cumin")),
            createIngredient("Paprika", "Spice", List.of("paprika")),
            createIngredient("Chili", "Spice", List.of("chili", "chili powder", "cayenne"))
        );

        ingredientRepository.saveAll(ingredients);
        log.info("Successfully seeded {} ingredients", ingredients.size());
    }

    private void seedRecipes() throws IOException {
        long count = recipeRepository.count();
        if (count > 0) {
            log.info("Recipes already seeded (count: {}), skipping...", count);
            return;
        }

        log.info("Seeding recipes from dataset...");
        
        ClassPathResource resource = new ClassPathResource("recipes-dataset.json");
        List<Map<String, Object>> recipeData = objectMapper.readValue(
            resource.getInputStream(),
            new TypeReference<List<Map<String, Object>>>() {}
        );

        List<Recipe> recipes = new ArrayList<>();
        for (Map<String, Object> data : recipeData) {
            Recipe recipe = new Recipe();
            recipe.setTitle((String) data.get("name"));
            recipe.setDescription((String) data.get("description"));
            recipe.setCuisine((String) data.get("cuisine"));
            recipe.setDifficulty((String) data.get("difficulty"));
            recipe.setPrepTime((Integer) data.get("prepTime"));
            recipe.setCookTime((Integer) data.get("cookTime"));
            recipe.setServings((Integer) data.get("servings"));
            recipe.setImageUrl((String) data.get("imageUrl"));
            recipe.setThumbnailUrl((String) data.get("imageUrl"));
            
            @SuppressWarnings("unchecked")
            List<String> ingredientNames = (List<String>) data.get("ingredients");
            List<Recipe.RecipeIngredient> recipeIngredients = new ArrayList<>();
            if (ingredientNames != null) {
                for (String name : ingredientNames) {
                    Recipe.RecipeIngredient ing = new Recipe.RecipeIngredient();
                    ing.setName(name);
                    ing.setQuantity(1.0);
                    ing.setUnit("unit");
                    recipeIngredients.add(ing);
                }
            }
            recipe.setIngredients(recipeIngredients);
            
            @SuppressWarnings("unchecked")
            List<String> instructions = (List<String>) data.get("instructions");
            recipe.setInstructions(instructions != null ? instructions : new ArrayList<>());
            
            recipe.setCreatedAt(java.time.LocalDateTime.now());
            recipe.setUpdatedAt(java.time.LocalDateTime.now());
            recipe.setIsApproved(true);
            recipe.setApprovedAt(java.time.LocalDateTime.now());
            recipe.setRatings(new Recipe.Ratings(4.5, Math.max(10, (int)(Math.random() * 100))));
            recipe.setAuthorName("Recipe Adjuster");
            
            recipes.add(recipe);
        }

        recipeRepository.saveAll(recipes);
        log.info("Successfully seeded {} recipes", recipes.size());
    }

    private Ingredient createIngredient(String name, String category, List<String> aliases) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(name);
        ingredient.setCategory(category);
        return ingredient;
    }
}
