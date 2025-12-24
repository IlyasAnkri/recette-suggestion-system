#!/usr/bin/env python3
"""
Generate comprehensive seed data for recipes and ingredients
Generates 50+ recipes across 5 cuisines and 200+ ingredients
"""
import json
import random

# Italian recipes (10)
italian_recipes = [
    {
        "title": "Classic Spaghetti Carbonara",
        "slug": "classic-spaghetti-carbonara",
        "description": "Traditional Italian pasta with eggs, cheese, and pancetta",
        "ingredients": [
            {"name": "spaghetti", "quantity": 400, "unit": "g", "optional": False},
            {"name": "pancetta", "quantity": 200, "unit": "g", "optional": False},
            {"name": "eggs", "quantity": 4, "unit": "whole", "optional": False},
            {"name": "parmesan cheese", "quantity": 100, "unit": "g", "optional": False},
            {"name": "black pepper", "quantity": 1, "unit": "tsp", "optional": False}
        ],
        "instructions": [
            {"stepNumber": 1, "description": "Cook spaghetti in salted boiling water until al dente", "durationMinutes": 10},
            {"stepNumber": 2, "description": "Fry pancetta until crispy", "durationMinutes": 5},
            {"stepNumber": 3, "description": "Mix eggs and parmesan in a bowl", "durationMinutes": 2},
            {"stepNumber": 4, "description": "Combine hot pasta with pancetta, then add egg mixture off heat", "durationMinutes": 3}
        ],
        "prepTime": 10,
        "cookTime": 20,
        "servings": 4,
        "difficulty": "MEDIUM",
        "cuisine": "Italian",
        "categories": ["Pasta", "Main Course"],
        "tags": ["italian", "pasta", "quick", "comfort-food"],
        "isApproved": True
    },
    {
        "title": "Margherita Pizza",
        "slug": "margherita-pizza",
        "description": "Classic Neapolitan pizza with tomato, mozzarella, and basil",
        "ingredients": [
            {"name": "pizza dough", "quantity": 500, "unit": "g", "optional": False},
            {"name": "tomato sauce", "quantity": 200, "unit": "ml", "optional": False},
            {"name": "mozzarella cheese", "quantity": 250, "unit": "g", "optional": False},
            {"name": "basil", "quantity": 10, "unit": "leaves", "optional": False},
            {"name": "olive oil", "quantity": 2, "unit": "tbsp", "optional": False}
        ],
        "instructions": [
            {"stepNumber": 1, "description": "Preheat oven to 250°C", "durationMinutes": 15},
            {"stepNumber": 2, "description": "Roll out pizza dough", "durationMinutes": 5},
            {"stepNumber": 3, "description": "Spread tomato sauce and add mozzarella", "durationMinutes": 3},
            {"stepNumber": 4, "description": "Bake for 10-12 minutes until crust is golden", "durationMinutes": 12}
        ],
        "prepTime": 20,
        "cookTime": 12,
        "servings": 2,
        "difficulty": "EASY",
        "cuisine": "Italian",
        "categories": ["Pizza", "Main Course"],
        "tags": ["italian", "pizza", "vegetarian"],
        "isApproved": True
    },
    {
        "title": "Lasagna Bolognese",
        "slug": "lasagna-bolognese",
        "description": "Layered pasta with rich meat sauce and béchamel",
        "ingredients": [
            {"name": "lasagna noodles", "quantity": 12, "unit": "sheets", "optional": False},
            {"name": "ground beef", "quantity": 500, "unit": "g", "optional": False},
            {"name": "tomato sauce", "quantity": 800, "unit": "ml", "optional": False},
            {"name": "onion", "quantity": 1, "unit": "whole", "optional": False},
            {"name": "garlic", "quantity": 3, "unit": "cloves", "optional": False},
            {"name": "mozzarella cheese", "quantity": 300, "unit": "g", "optional": False},
            {"name": "parmesan cheese", "quantity": 100, "unit": "g", "optional": False},
            {"name": "butter", "quantity": 50, "unit": "g", "optional": False},
            {"name": "flour", "quantity": 50, "unit": "g", "optional": False},
            {"name": "milk", "quantity": 500, "unit": "ml", "optional": False}
        ],
        "instructions": [
            {"stepNumber": 1, "description": "Prepare Bolognese sauce with beef, tomatoes, onion, and garlic", "durationMinutes": 30},
            {"stepNumber": 2, "description": "Make béchamel sauce with butter, flour, and milk", "durationMinutes": 15},
            {"stepNumber": 3, "description": "Layer noodles, Bolognese, béchamel, and cheese", "durationMinutes": 20},
            {"stepNumber": 4, "description": "Bake at 180°C for 40 minutes", "durationMinutes": 40}
        ],
        "prepTime": 45,
        "cookTime": 70,
        "servings": 8,
        "difficulty": "HARD",
        "cuisine": "Italian",
        "categories": ["Pasta", "Main Course"],
        "tags": ["italian", "pasta", "baked", "comfort-food"],
        "isApproved": True
    },
    {
        "title": "Risotto alla Milanese",
        "slug": "risotto-alla-milanese",
        "description": "Creamy saffron risotto from Milan",
        "ingredients": [
            {"name": "arborio rice", "quantity": 320, "unit": "g", "optional": False},
            {"name": "chicken broth", "quantity": 1000, "unit": "ml", "optional": False},
            {"name": "white wine", "quantity": 150, "unit": "ml", "optional": False},
            {"name": "onion", "quantity": 1, "unit": "whole", "optional": False},
            {"name": "butter", "quantity": 80, "unit": "g", "optional": False},
            {"name": "parmesan cheese", "quantity": 80, "unit": "g", "optional": False},
            {"name": "saffron", "quantity": 0.5, "unit": "g", "optional": False}
        ],
        "instructions": [
            {"stepNumber": 1, "description": "Sauté onion in butter until soft", "durationMinutes": 5},
            {"stepNumber": 2, "description": "Add rice and toast for 2 minutes", "durationMinutes": 2},
            {"stepNumber": 3, "description": "Add wine and let evaporate", "durationMinutes": 3},
            {"stepNumber": 4, "description": "Add broth gradually, stirring constantly with saffron", "durationMinutes": 20},
            {"stepNumber": 5, "description": "Finish with butter and parmesan", "durationMinutes": 2}
        ],
        "prepTime": 10,
        "cookTime": 32,
        "servings": 4,
        "difficulty": "MEDIUM",
        "cuisine": "Italian",
        "categories": ["Rice", "Main Course"],
        "tags": ["italian", "risotto", "vegetarian", "creamy"],
        "isApproved": True
    },
    {
        "title": "Penne Arrabbiata",
        "slug": "penne-arrabbiata",
        "description": "Spicy tomato pasta with garlic and chili",
        "ingredients": [
            {"name": "penne pasta", "quantity": 400, "unit": "g", "optional": False},
            {"name": "tomato", "quantity": 6, "unit": "whole", "optional": False},
            {"name": "garlic", "quantity": 4, "unit": "cloves", "optional": False},
            {"name": "red chili flakes", "quantity": 2, "unit": "tsp", "optional": False},
            {"name": "olive oil", "quantity": 4, "unit": "tbsp", "optional": False},
            {"name": "basil", "quantity": 10, "unit": "leaves", "optional": False}
        ],
        "instructions": [
            {"stepNumber": 1, "description": "Cook penne until al dente", "durationMinutes": 11},
            {"stepNumber": 2, "description": "Sauté garlic and chili in olive oil", "durationMinutes": 2},
            {"stepNumber": 3, "description": "Add crushed tomatoes and simmer", "durationMinutes": 15},
            {"stepNumber": 4, "description": "Toss pasta with sauce and basil", "durationMinutes": 2}
        ],
        "prepTime": 10,
        "cookTime": 30,
        "servings": 4,
        "difficulty": "EASY",
        "cuisine": "Italian",
        "categories": ["Pasta", "Main Course"],
        "tags": ["italian", "pasta", "spicy", "vegan"],
        "isApproved": True
    },
    {
        "title": "Osso Buco",
        "slug": "osso-buco",
        "description": "Braised veal shanks in white wine and vegetables",
        "ingredients": [
            {"name": "veal shanks", "quantity": 4, "unit": "pieces", "optional": False},
            {"name": "white wine", "quantity": 250, "unit": "ml", "optional": False},
            {"name": "chicken broth", "quantity": 500, "unit": "ml", "optional": False},
            {"name": "tomato", "quantity": 4, "unit": "whole", "optional": False},
            {"name": "carrot", "quantity": 2, "unit": "whole", "optional": False},
            {"name": "celery", "quantity": 2, "unit": "stalks", "optional": False},
            {"name": "onion", "quantity": 1, "unit": "whole", "optional": False},
            {"name": "garlic", "quantity": 3, "unit": "cloves", "optional": False},
            {"name": "flour", "quantity": 50, "unit": "g", "optional": False},
            {"name": "olive oil", "quantity": 3, "unit": "tbsp", "optional": False}
        ],
        "instructions": [
            {"stepNumber": 1, "description": "Dredge veal in flour and brown in oil", "durationMinutes": 10},
            {"stepNumber": 2, "description": "Sauté vegetables until soft", "durationMinutes": 8},
            {"stepNumber": 3, "description": "Add wine and reduce", "durationMinutes": 5},
            {"stepNumber": 4, "description": "Add broth and tomatoes, braise for 2 hours", "durationMinutes": 120}
        ],
        "prepTime": 20,
        "cookTime": 143,
        "servings": 4,
        "difficulty": "HARD",
        "cuisine": "Italian",
        "categories": ["Meat", "Main Course"],
        "tags": ["italian", "braised", "veal", "slow-cooked"],
        "isApproved": True
    },
    {
        "title": "Caprese Salad",
        "slug": "caprese-salad",
        "description": "Fresh mozzarella, tomatoes, and basil with balsamic",
        "ingredients": [
            {"name": "mozzarella cheese", "quantity": 250, "unit": "g", "optional": False},
            {"name": "tomato", "quantity": 4, "unit": "whole", "optional": False},
            {"name": "basil", "quantity": 20, "unit": "leaves", "optional": False},
            {"name": "olive oil", "quantity": 3, "unit": "tbsp", "optional": False},
            {"name": "balsamic vinegar", "quantity": 2, "unit": "tbsp", "optional": False},
            {"name": "salt", "quantity": 1, "unit": "tsp", "optional": False}
        ],
        "instructions": [
            {"stepNumber": 1, "description": "Slice tomatoes and mozzarella", "durationMinutes": 5},
            {"stepNumber": 2, "description": "Arrange alternating slices with basil", "durationMinutes": 3},
            {"stepNumber": 3, "description": "Drizzle with oil and balsamic", "durationMinutes": 1},
            {"stepNumber": 4, "description": "Season with salt", "durationMinutes": 1}
        ],
        "prepTime": 10,
        "cookTime": 0,
        "servings": 4,
        "difficulty": "EASY",
        "cuisine": "Italian",
        "categories": ["Salad", "Appetizer"],
        "tags": ["italian", "salad", "vegetarian", "no-cook", "fresh"],
        "isApproved": True
    },
    {
        "title": "Minestrone Soup",
        "slug": "minestrone-soup",
        "description": "Hearty Italian vegetable soup with pasta",
        "ingredients": [
            {"name": "cannellini beans", "quantity": 200, "unit": "g", "optional": False},
            {"name": "tomato", "quantity": 4, "unit": "whole", "optional": False},
            {"name": "carrot", "quantity": 2, "unit": "whole", "optional": False},
            {"name": "celery", "quantity": 2, "unit": "stalks", "optional": False},
            {"name": "zucchini", "quantity": 1, "unit": "whole", "optional": False},
            {"name": "onion", "quantity": 1, "unit": "whole", "optional": False},
            {"name": "garlic", "quantity": 3, "unit": "cloves", "optional": False},
            {"name": "small pasta", "quantity": 100, "unit": "g", "optional": False},
            {"name": "vegetable broth", "quantity": 1500, "unit": "ml", "optional": False},
            {"name": "olive oil", "quantity": 3, "unit": "tbsp", "optional": False}
        ],
        "instructions": [
            {"stepNumber": 1, "description": "Sauté onion, garlic, carrot, and celery", "durationMinutes": 8},
            {"stepNumber": 2, "description": "Add tomatoes and broth, bring to boil", "durationMinutes": 10},
            {"stepNumber": 3, "description": "Add beans and zucchini, simmer", "durationMinutes": 20},
            {"stepNumber": 4, "description": "Add pasta and cook until tender", "durationMinutes": 10}
        ],
        "prepTime": 15,
        "cookTime": 48,
        "servings": 6,
        "difficulty": "EASY",
        "cuisine": "Italian",
        "categories": ["Soup", "Main Course"],
        "tags": ["italian", "soup", "vegetarian", "healthy"],
        "isApproved": True
    },
    {
        "title": "Tiramisu",
        "slug": "tiramisu",
        "description": "Classic Italian coffee-flavored dessert",
        "ingredients": [
            {"name": "ladyfinger cookies", "quantity": 24, "unit": "pieces", "optional": False},
            {"name": "mascarpone cheese", "quantity": 500, "unit": "g", "optional": False},
            {"name": "eggs", "quantity": 4, "unit": "whole", "optional": False},
            {"name": "sugar", "quantity": 100, "unit": "g", "optional": False},
            {"name": "espresso coffee", "quantity": 300, "unit": "ml", "optional": False},
            {"name": "cocoa powder", "quantity": 2, "unit": "tbsp", "optional": False}
        ],
        "instructions": [
            {"stepNumber": 1, "description": "Separate eggs and beat yolks with sugar", "durationMinutes": 5},
            {"stepNumber": 2, "description": "Fold in mascarpone", "durationMinutes": 3},
            {"stepNumber": 3, "description": "Dip ladyfingers in espresso and layer", "durationMinutes": 10},
            {"stepNumber": 4, "description": "Refrigerate for 4 hours, dust with cocoa", "durationMinutes": 240}
        ],
        "prepTime": 20,
        "cookTime": 0,
        "servings": 8,
        "difficulty": "MEDIUM",
        "cuisine": "Italian",
        "categories": ["Dessert"],
        "tags": ["italian", "dessert", "coffee", "no-bake"],
        "isApproved": True
    },
    {
        "title": "Bruschetta",
        "slug": "bruschetta",
        "description": "Grilled bread with tomato, garlic, and basil",
        "ingredients": [
            {"name": "bread", "quantity": 8, "unit": "slices", "optional": False},
            {"name": "tomato", "quantity": 4, "unit": "whole", "optional": False},
            {"name": "garlic", "quantity": 2, "unit": "cloves", "optional": False},
            {"name": "basil", "quantity": 10, "unit": "leaves", "optional": False},
            {"name": "olive oil", "quantity": 4, "unit": "tbsp", "optional": False},
            {"name": "balsamic vinegar", "quantity": 1, "unit": "tbsp", "optional": False}
        ],
        "instructions": [
            {"stepNumber": 1, "description": "Grill or toast bread slices", "durationMinutes": 5},
            {"stepNumber": 2, "description": "Rub with garlic clove", "durationMinutes": 2},
            {"stepNumber": 3, "description": "Top with diced tomatoes and basil", "durationMinutes": 5},
            {"stepNumber": 4, "description": "Drizzle with oil and balsamic", "durationMinutes": 1}
        ],
        "prepTime": 10,
        "cookTime": 5,
        "servings": 4,
        "difficulty": "EASY",
        "cuisine": "Italian",
        "categories": ["Appetizer"],
        "tags": ["italian", "appetizer", "vegan", "quick"],
        "isApproved": True
    }
]

print(f"Generated {len(italian_recipes)} Italian recipes")
print(json.dumps(italian_recipes[:2], indent=2))

# TODO: Add Mexican, Asian, American, Mediterranean recipes (40 more recipes)
# TODO: Add comprehensive ingredient list (200+ ingredients)

# For now, write the Italian recipes to demonstrate the pattern
# Full implementation should include all 5 cuisines and 200+ ingredients

# Write recipes to file
recipes_output_path = "services/shared/src/main/resources/seed-data/recipes.json"
with open(recipes_output_path, 'w', encoding='utf-8') as f:
    json.dump(italian_recipes, f, indent=2, ensure_ascii=False)
print(f"\nWrote {len(italian_recipes)} recipes to {recipes_output_path}")

# Note: Ingredient generation and full recipe set implementation pending
print("\nNote: This script currently generates only Italian recipes (10).")
print("Full implementation requires:")
print("- 40 more recipes (Mexican, Asian, American, Mediterranean)")
print("- 200+ comprehensive ingredient list with aliases and categories")
