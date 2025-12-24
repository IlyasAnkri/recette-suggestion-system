import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { RecipeCardComponent, Recipe } from '../../shared/components/recipe-card/recipe-card.component';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-recipe-search',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatCardModule, MatButtonModule, RecipeCardComponent, LoadingSpinnerComponent],
  templateUrl: './recipe-search.component.html',
  styleUrls: ['./recipe-search.component.scss']
})
export class RecipeSearchComponent implements OnInit {
  recipes = signal<Recipe[]>([]);
  loading = signal(false);
  hasMore = signal(true);

  // Ingredient synonyms and categories for better matching
  private ingredientSynonyms: { [key: string]: string[] } = {
    'meat': ['beef', 'pork', 'chicken', 'lamb', 'turkey', 'veal', 'bacon', 'ham', 'sausage'],
    'beef': ['meat', 'steak', 'ground beef', 'sirloin'],
    'pork': ['meat', 'bacon', 'ham', 'sausage'],
    'chicken': ['meat', 'poultry', 'turkey'],
    'poultry': ['chicken', 'turkey', 'duck'],
    'fish': ['salmon', 'tuna', 'cod', 'tilapia', 'seafood'],
    'seafood': ['fish', 'shrimp', 'crab', 'lobster', 'shellfish', 'salmon', 'tuna'],
    'shrimp': ['seafood', 'prawns'],
    'pasta': ['spaghetti', 'penne', 'linguine', 'noodles', 'macaroni'],
    'noodles': ['pasta', 'spaghetti', 'ramen'],
    'cheese': ['parmesan', 'mozzarella', 'cheddar', 'feta', 'gouda'],
    'parmesan': ['cheese'],
    'mozzarella': ['cheese'],
    'feta': ['cheese'],
    'vegetable': ['carrot', 'broccoli', 'potato', 'onion', 'tomato', 'pepper', 'cucumber'],
    'vegetables': ['carrot', 'broccoli', 'potato', 'onion', 'tomato', 'pepper', 'cucumber'],
    'tomato': ['vegetable', 'tomatoes'],
    'potato': ['vegetable', 'potatoes'],
    'onion': ['vegetable', 'onions'],
    'garlic': ['vegetable'],
    'rice': ['grain', 'arborio'],
    'grain': ['rice', 'quinoa', 'barley'],
    'herb': ['basil', 'parsley', 'cilantro', 'dill', 'oregano', 'thyme'],
    'herbs': ['basil', 'parsley', 'cilantro', 'dill', 'oregano', 'thyme'],
    'basil': ['herb'],
    'parsley': ['herb'],
    'cilantro': ['herb', 'coriander'],
    'oil': ['olive oil', 'vegetable oil', 'sesame oil', 'coconut oil'],
    'olive oil': ['oil'],
  };

  private allRecipes: Recipe[] = [
    {
      id: '1',
      title: 'Classic Chicken Parmesan',
      thumbnail: 'https://images.unsplash.com/photo-1632778149955-e80f8ceca2e8?w=400&h=300&fit=crop',
      matchPercentage: 95,
      missingIngredientsCount: 1,
      cuisine: 'Italian',
      difficulty: 'Medium',
      prepTime: 45,
      ingredients: ['chicken', 'parmesan', 'tomato', 'basil', 'breadcrumbs', 'egg', 'flour', 'olive oil']
    },
    {
      id: '2',
      title: 'Garlic Butter Shrimp Pasta',
      thumbnail: 'https://images.unsplash.com/photo-1563379926898-05f4575a45d8?w=400&h=300&fit=crop',
      matchPercentage: 88,
      missingIngredientsCount: 2,
      cuisine: 'Italian',
      difficulty: 'Easy',
      prepTime: 30,
      ingredients: ['shrimp', 'pasta', 'garlic', 'butter', 'parsley', 'lemon', 'olive oil']
    },
    {
      id: '3',
      title: 'Beef Stir Fry with Vegetables',
      thumbnail: 'https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=400&h=300&fit=crop',
      matchPercentage: 82,
      missingIngredientsCount: 3,
      cuisine: 'Asian',
      difficulty: 'Easy',
      prepTime: 25,
      ingredients: ['beef', 'broccoli', 'carrot', 'soy sauce', 'ginger', 'garlic', 'sesame oil']
    },
    {
      id: '4',
      title: 'Margherita Pizza',
      thumbnail: 'https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=400&h=300&fit=crop',
      matchPercentage: 75,
      missingIngredientsCount: 4,
      cuisine: 'Italian',
      difficulty: 'Medium',
      prepTime: 60,
      ingredients: ['flour', 'tomato', 'mozzarella', 'basil', 'olive oil', 'yeast', 'salt']
    },
    {
      id: '5',
      title: 'Greek Salad with Grilled Chicken',
      thumbnail: 'https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=400&h=300&fit=crop',
      matchPercentage: 70,
      missingIngredientsCount: 5,
      cuisine: 'Greek',
      difficulty: 'Easy',
      prepTime: 20,
      ingredients: ['chicken', 'cucumber', 'tomato', 'feta', 'olive', 'onion', 'olive oil', 'lemon']
    },
    {
      id: '6',
      title: 'Vegetable Curry',
      thumbnail: 'https://images.unsplash.com/photo-1455619452474-d2be8b1e70cd?w=400&h=300&fit=crop',
      matchPercentage: 65,
      missingIngredientsCount: 6,
      cuisine: 'Indian',
      difficulty: 'Medium',
      prepTime: 40,
      ingredients: ['potato', 'carrot', 'peas', 'coconut milk', 'curry powder', 'onion', 'garlic', 'ginger']
    },
    {
      id: '7',
      title: 'Grilled Salmon with Lemon',
      thumbnail: 'https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=400&h=300&fit=crop',
      matchPercentage: 85,
      missingIngredientsCount: 2,
      cuisine: 'American',
      difficulty: 'Easy',
      prepTime: 25,
      ingredients: ['salmon', 'lemon', 'dill', 'garlic', 'olive oil', 'salt', 'pepper']
    },
    {
      id: '8',
      title: 'Mushroom Risotto',
      thumbnail: 'https://images.unsplash.com/photo-1476124369491-e7addf5db371?w=400&h=300&fit=crop',
      matchPercentage: 78,
      missingIngredientsCount: 3,
      cuisine: 'Italian',
      difficulty: 'Medium',
      prepTime: 45,
      ingredients: ['rice', 'mushroom', 'parmesan', 'butter', 'onion', 'garlic', 'white wine', 'broth']
    },
    {
      id: '9',
      title: 'Tacos al Pastor',
      thumbnail: 'https://images.unsplash.com/photo-1565299585323-38d6b0865b47?w=400&h=300&fit=crop',
      matchPercentage: 72,
      missingIngredientsCount: 4,
      cuisine: 'Mexican',
      difficulty: 'Medium',
      prepTime: 35,
      ingredients: ['pork', 'pineapple', 'onion', 'cilantro', 'lime', 'tortilla', 'achiote']
    },
    {
      id: '10',
      title: 'Tom Yum Soup',
      thumbnail: 'https://images.unsplash.com/photo-1548943487-a2e4e43b4853?w=400&h=300&fit=crop',
      matchPercentage: 68,
      missingIngredientsCount: 5,
      cuisine: 'Thai',
      difficulty: 'Medium',
      prepTime: 30,
      ingredients: ['shrimp', 'mushroom', 'lemongrass', 'lime', 'chili', 'fish sauce', 'galangal']
    }
  ];
  
  private userIngredients: string[] = [];

  constructor(private router: Router) {}

  ngOnInit(): void {
    // Get user ingredients from localStorage
    if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
      const storedIngredients = localStorage.getItem('ingredients');
      if (storedIngredients) {
        this.userIngredients = JSON.parse(storedIngredients);
      }
    }
    this.loadRecipes();
  }

  loadRecipes(): void {
    this.loading.set(true);
    
    setTimeout(() => {
      const filteredRecipes = this.filterAndScoreRecipes();
      this.recipes.set(filteredRecipes);
      this.loading.set(false);
      this.hasMore.set(false);
    }, 500);
  }
  
  // Get all possible matches for a user ingredient (including synonyms)
  private getExpandedIngredients(userIngredient: string): string[] {
    const lower = userIngredient.toLowerCase().trim();
    const expanded = [lower];
    
    // Add direct synonyms
    if (this.ingredientSynonyms[lower]) {
      expanded.push(...this.ingredientSynonyms[lower]);
    }
    
    // Check if any synonym category contains this ingredient
    for (const [category, synonyms] of Object.entries(this.ingredientSynonyms)) {
      if (synonyms.includes(lower) && !expanded.includes(category)) {
        expanded.push(category);
      }
    }
    
    return expanded;
  }

  // Check if a recipe ingredient matches any user ingredient (with synonyms)
  private ingredientMatches(recipeIngredient: string, userIngredients: string[]): boolean {
    const recipeIngLower = recipeIngredient.toLowerCase();
    
    for (const userIng of userIngredients) {
      const expandedIngredients = this.getExpandedIngredients(userIng);
      
      for (const expanded of expandedIngredients) {
        // Direct match
        if (recipeIngLower === expanded) return true;
        
        // Partial match (recipe ingredient contains user ingredient)
        if (recipeIngLower.includes(expanded)) return true;
        
        // Word-based match
        const words = recipeIngLower.split(/\s+/);
        if (words.some(word => word === expanded || expanded.includes(word))) return true;
      }
    }
    
    return false;
  }

  private filterAndScoreRecipes(): Recipe[] {
    if (this.userIngredients.length === 0) {
      return this.allRecipes;
    }
    
    const scoredRecipes = this.allRecipes.map(recipe => {
      const recipeIngredients = (recipe as any).ingredients || [];
      
      // Match ingredients using synonym system
      const matchedIngredients = recipeIngredients.filter((recipeIng: string) => 
        this.ingredientMatches(recipeIng, this.userIngredients)
      );
      
      const matchPercentage = recipeIngredients.length > 0 
        ? Math.round((matchedIngredients.length / recipeIngredients.length) * 100)
        : 0;
      const missingCount = recipeIngredients.length - matchedIngredients.length;
      
      return {
        ...recipe,
        matchPercentage,
        missingIngredientsCount: missingCount,
        matchedCount: matchedIngredients.length,
        matchedIngredientsList: matchedIngredients
      };
    });
    
    // Filter out recipes with no matching ingredients
    const filteredRecipes = scoredRecipes.filter(recipe => 
      (recipe as any).matchedCount > 0
    );
    
    // If no recipes match, return all recipes sorted by default order
    if (filteredRecipes.length === 0) {
      return this.allRecipes;
    }
    
    // Sort by match percentage descending, then by number of missing ingredients ascending
    return filteredRecipes.sort((a, b) => {
      if (b.matchPercentage !== a.matchPercentage) {
        return b.matchPercentage - a.matchPercentage;
      }
      return a.missingIngredientsCount - b.missingIngredientsCount;
    });
  }

  onRecipeClick(recipeId: string): void {
    this.router.navigate(['/recipes', recipeId]);
  }

  onScroll(): void {
    if (!this.loading() && this.hasMore()) {
      this.loadRecipes();
    }
  }
}
