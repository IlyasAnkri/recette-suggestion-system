import { Component, OnInit, signal, Inject, PLATFORM_ID, Optional } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialogModule } from '@angular/material/dialog';

interface RecipeDetail {
  id: string;
  title: string;
  image: string;
  description: string;
  prepTime: number;
  cookTime: number;
  servings: number;
  difficulty: string;
  cuisine: string;
  ingredients: { name: string; amount: string; available: boolean }[];
  instructions: string[];
  nutrition: { calories: number; protein: string; carbs: string; fat: string };
}

@Component({
  selector: 'app-recipe-detail',
  standalone: true,
  imports: [
    CommonModule,
    HttpClientModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    MatDialogModule
  ],
  template: `
    <div class="recipe-page">
      <div class="debug-info" *ngIf="!recipe && !loading">
        <p>Recipe not found. Loading: {{ loading }}, Recipe ID from URL: {{ currentRecipeId }}</p>
      </div>
      <div class="recipe-hero" *ngIf="recipe">
        <div class="hero-image" [style.backgroundImage]="'url(' + recipe.image + ')'">
          <div class="hero-overlay">
            <div class="hero-content">
              <span class="cuisine-badge">{{ recipe.cuisine }}</span>
              <h1 class="recipe-title">{{ recipe.title }}</h1>
              <p class="recipe-desc">{{ recipe.description }}</p>
              <div class="hero-actions">
                <button mat-fab color="accent" (click)="toggleSaveRecipe()" [attr.aria-label]="isSaved() ? 'Remove from saved' : 'Save recipe'">
                  <mat-icon>{{ isSaved() ? 'bookmark' : 'bookmark_border' }}</mat-icon>
                </button>
              </div>
              <div class="recipe-stats">
                <div class="stat">
                  <mat-icon>schedule</mat-icon>
                  <span>{{ recipe.prepTime }} min prep</span>
                </div>
                <div class="stat">
                  <mat-icon>timer</mat-icon>
                  <span>{{ recipe.cookTime }} min cook</span>
                </div>
                <div class="stat">
                  <mat-icon>people</mat-icon>
                  <span>{{ recipe.servings }} servings</span>
                </div>
                <div class="stat">
                  <mat-icon>trending_up</mat-icon>
                  <span>{{ recipe.difficulty }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="recipe-content" *ngIf="recipe">
        <div class="content-grid">
          <!-- Ingredients Card -->
          <div class="ingredients-section">
            <div class="section-card">
              <div class="section-header">
                <mat-icon>shopping_basket</mat-icon>
                <h2>Ingredients</h2>
              </div>
              <ul class="ingredients-list">
                <li *ngFor="let ing of recipe.ingredients" [class.missing]="!ing.available">
                  <mat-icon class="status-icon">{{ ing.available ? 'check_circle' : 'cancel' }}</mat-icon>
                  <span class="amount">{{ ing.amount }}</span>
                  <span class="name">{{ ing.name }}</span>
                </li>
              </ul>
              <button mat-raised-button color="warn" class="substitution-btn" *ngIf="getMissingCount() > 0" (click)="findSubstitutions()">
                <mat-icon class="button-icon">swap_horiz</mat-icon>
                <span class="button-text">Find {{ getMissingCount() }} Substitutions</span>
              </button>
            </div>

            <!-- Nutrition Card -->
            <div class="section-card nutrition-card">
              <div class="section-header">
                <mat-icon>local_fire_department</mat-icon>
                <h2>Nutrition</h2>
              </div>
              <div class="nutrition-grid">
                <div class="nutrition-item calories">
                  <span class="value">{{ recipe.nutrition.calories }}</span>
                  <span class="label">Calories</span>
                </div>
                <div class="nutrition-item protein">
                  <span class="value">{{ recipe.nutrition.protein }}</span>
                  <span class="label">Protein</span>
                </div>
                <div class="nutrition-item carbs">
                  <span class="value">{{ recipe.nutrition.carbs }}</span>
                  <span class="label">Carbs</span>
                </div>
                <div class="nutrition-item fat">
                  <span class="value">{{ recipe.nutrition.fat }}</span>
                  <span class="label">Fat</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Instructions -->
          <div class="instructions-section">
            <div class="section-card">
              <div class="section-header">
                <mat-icon>menu_book</mat-icon>
                <h2>Instructions</h2>
              </div>
              <ol class="instructions-list">
                <li *ngFor="let step of recipe.instructions; let i = index">
                  <div class="step-number">{{ i + 1 }}</div>
                  <div class="step-text">{{ step }}</div>
                </li>
              </ol>
            </div>
          </div>
        </div>
      </div>

      <div class="loading-state" *ngIf="loading">
        <mat-spinner diameter="60"></mat-spinner>
        <p>Loading recipe...</p>
      </div>
    </div>
  `,
  styles: [`
    .recipe-page {
      min-height: 100vh;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }

    .hero-image {
      height: 450px;
      background-size: cover;
      background-position: center;
      position: relative;
    }

    .hero-overlay {
      height: 100%;
      background: linear-gradient(to top, rgba(0,0,0,0.9) 0%, rgba(0,0,0,0.3) 50%, rgba(0,0,0,0.1) 100%);
      display: flex;
      align-items: flex-end;
    }

    .hero-content {
      padding: 3rem;
      color: white;
      max-width: 900px;
    }

    .cuisine-badge {
      display: inline-block;
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      padding: 8px 20px;
      border-radius: 25px;
      font-size: 0.85rem;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 1px;
      margin-bottom: 1rem;
    }

    .recipe-title {
      font-size: 3rem;
      font-weight: 800;
      margin: 0 0 1rem 0;
      line-height: 1.1;
      text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
    }

    .recipe-desc {
      font-size: 1.2rem;
      opacity: 0.9;
      margin: 0 0 1.5rem 0;
      line-height: 1.6;
    }

    .hero-actions {
      margin-bottom: 1.5rem;
      
      button {
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
        transition: transform 0.2s ease, box-shadow 0.2s ease;
        
        &:hover {
          transform: scale(1.1);
          box-shadow: 0 6px 30px rgba(0, 0, 0, 0.4);
        }
        
        mat-icon {
          font-size: 28px;
          width: 28px;
          height: 28px;
        }
      }
    }

    .recipe-stats {
      display: flex;
      gap: 2rem;
      flex-wrap: wrap;
    }

    .stat {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      background: rgba(255,255,255,0.15);
      padding: 10px 20px;
      border-radius: 30px;
      backdrop-filter: blur(10px);

      mat-icon {
        font-size: 1.25rem;
        width: 1.25rem;
        height: 1.25rem;
      }
    }

    .recipe-content {
      padding: 2rem;
      margin-top: -50px;
      position: relative;
    }

    .content-grid {
      display: grid;
      grid-template-columns: 380px 1fr;
      gap: 2rem;
      max-width: 1400px;
      margin: 0 auto;
    }

    .section-card {
      background: white;
      border-radius: 24px;
      padding: 2rem;
      box-shadow: 0 20px 60px rgba(0,0,0,0.15);
      margin-bottom: 2rem;
    }

    .section-header {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      margin-bottom: 1.5rem;
      padding-bottom: 1rem;
      border-bottom: 2px solid #f0f0f0;

      mat-icon {
        font-size: 1.75rem;
        width: 1.75rem;
        height: 1.75rem;
        color: #667eea;
      }

      h2 {
        margin: 0;
        font-size: 1.5rem;
        font-weight: 700;
        color: #333;
      }
    }

    .ingredients-list {
      list-style: none;
      padding: 0;
      margin: 0;

      li {
        display: flex;
        align-items: center;
        gap: 1rem;
        padding: 1rem;
        border-radius: 12px;
        margin-bottom: 0.5rem;
        background: #f8f9fa;
        transition: all 0.2s ease;

        &:hover {
          background: #e9ecef;
          transform: translateX(5px);
        }

        &.missing {
          background: #fff5f5;
          border-left: 4px solid #f44336;
        }
      }

      .status-icon {
        font-size: 1.25rem;
        width: 1.25rem;
        height: 1.25rem;
      }

      li:not(.missing) .status-icon {
        color: #4caf50;
      }

      li.missing .status-icon {
        color: #f44336;
      }

      .amount {
        font-weight: 600;
        min-width: 80px;
        color: #667eea;
      }

      .name {
        flex: 1;
        color: #333;
      }
    }

    .substitution-btn {
      width: 100%;
      margin-top: 1.5rem;
      height: 50px;
      font-size: 1rem;
      border-radius: 12px;
      display: flex !important;
      align-items: center !important;
      justify-content: center !important;
      gap: 0.5rem !important;
      
      .button-icon {
        margin: 0 !important;
        font-size: 20px !important;
        width: 20px !important;
        height: 20px !important;
        line-height: 20px !important;
      }
      
      .button-text {
        line-height: normal;
      }
    }

    .nutrition-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 1rem;
    }

    .nutrition-item {
      text-align: center;
      padding: 1.5rem 1rem;
      border-radius: 16px;
      
      &.calories {
        background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
      }
      &.protein {
        background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
      }
      &.carbs {
        background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
      }
      &.fat {
        background: linear-gradient(135deg, #d299c2 0%, #fef9d7 100%);
      }

      .value {
        display: block;
        font-size: 1.75rem;
        font-weight: 800;
        color: #333;
      }

      .label {
        display: block;
        font-size: 0.85rem;
        color: #666;
        text-transform: uppercase;
        letter-spacing: 1px;
        margin-top: 0.25rem;
      }
    }

    .instructions-list {
      list-style: none;
      padding: 0;
      margin: 0;
      counter-reset: step;

      li {
        display: flex;
        gap: 1.5rem;
        padding: 1.5rem 0;
        border-bottom: 1px solid #f0f0f0;

        &:last-child {
          border-bottom: none;
        }
      }

      .step-number {
        width: 45px;
        height: 45px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: 700;
        font-size: 1.1rem;
        flex-shrink: 0;
      }

      .step-text {
        flex: 1;
        line-height: 1.7;
        color: #444;
        font-size: 1.05rem;
        padding-top: 8px;
      }
    }

    .loading-state {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      min-height: 60vh;
      color: white;

      p {
        margin-top: 1.5rem;
        font-size: 1.25rem;
      }
    }

    @media (max-width: 1024px) {
      .content-grid {
        grid-template-columns: 1fr;
      }

      .recipe-title {
        font-size: 2rem;
      }

      .hero-image {
        height: 350px;
      }
    }

    @media (max-width: 600px) {
      .hero-content {
        padding: 1.5rem;
      }

      .recipe-title {
        font-size: 1.75rem;
      }

      .recipe-stats {
        gap: 0.75rem;
      }

      .stat {
        padding: 8px 12px;
        font-size: 0.85rem;
      }

      .recipe-content {
        padding: 1rem;
      }

      .section-card {
        padding: 1.5rem;
        border-radius: 16px;
      }
    }
  `]
})
export class RecipeDetailComponent implements OnInit {
  recipe: RecipeDetail | null = null;
  loading = true;
  isSaved = signal(false);
  currentRecipeId: string = '';

  private recipes: { [key: string]: RecipeDetail } = {
    '1': {
      id: '1',
      title: 'Classic Chicken Parmesan',
      image: 'https://images.unsplash.com/photo-1632778149955-e80f8ceca2e8?w=1200&h=800&fit=crop',
      description: 'A delicious Italian-American dish featuring crispy breaded chicken cutlets topped with rich marinara sauce and melted mozzarella cheese. Perfect for a hearty family dinner.',
      prepTime: 20,
      cookTime: 25,
      servings: 4,
      difficulty: 'Medium',
      cuisine: 'Italian',
      ingredients: [
        { name: 'Chicken breast', amount: '4 pieces', available: true },
        { name: 'Breadcrumbs', amount: '1 cup', available: true },
        { name: 'Parmesan cheese', amount: '1/2 cup', available: true },
        { name: 'Mozzarella cheese', amount: '1 cup', available: true },
        { name: 'Marinara sauce', amount: '2 cups', available: false },
        { name: 'Eggs', amount: '2', available: true },
        { name: 'Olive oil', amount: '1/4 cup', available: true },
        { name: 'Italian seasoning', amount: '1 tsp', available: false }
      ],
      instructions: [
        'Preheat oven to 400°F (200°C) and line a baking sheet with parchment paper.',
        'Pound chicken breasts to even thickness (about 1/2 inch) for even cooking.',
        'Set up a breading station: flour in one bowl, beaten eggs in another, and breadcrumb mixture in a third.',
        'Season chicken with salt and pepper, then coat in flour, dip in egg, and press into breadcrumbs.',
        'Heat olive oil in a large oven-safe skillet over medium-high heat until shimmering.',
        'Cook breaded chicken for 3-4 minutes per side until golden brown and crispy.',
        'Top each chicken piece with marinara sauce and a generous amount of mozzarella cheese.',
        'Transfer to oven and bake for 15-20 minutes until cheese is melted and bubbly.',
        'Garnish with fresh basil leaves and serve immediately with pasta or crusty bread.'
      ],
      nutrition: { calories: 520, protein: '45g', carbs: '32g', fat: '22g' }
    },
    '2': {
      id: '2',
      title: 'Garlic Butter Shrimp Pasta',
      image: 'https://images.unsplash.com/photo-1563379926898-05f4575a45d8?w=1200&h=800&fit=crop',
      description: 'Quick and flavorful pasta dish with succulent shrimp in a rich garlic butter sauce. Ready in 30 minutes!',
      prepTime: 15,
      cookTime: 15,
      servings: 4,
      difficulty: 'Easy',
      cuisine: 'Italian',
      ingredients: [
        { name: 'Shrimp', amount: '1 lb', available: true },
        { name: 'Pasta', amount: '12 oz', available: true },
        { name: 'Garlic', amount: '6 cloves', available: true },
        { name: 'Butter', amount: '4 tbsp', available: true },
        { name: 'Parsley', amount: '1/4 cup', available: false },
        { name: 'Lemon', amount: '1', available: true },
        { name: 'Olive oil', amount: '2 tbsp', available: true }
      ],
      instructions: [
        'Cook pasta according to package directions until al dente.',
        'Heat olive oil and butter in a large skillet over medium heat.',
        'Add minced garlic and cook until fragrant, about 1 minute.',
        'Add shrimp and cook until pink, about 3-4 minutes per side.',
        'Drain pasta, reserving 1 cup pasta water, and add to skillet.',
        'Toss everything together, adding pasta water as needed.',
        'Finish with lemon juice and fresh parsley.',
        'Season with salt and pepper to taste and serve immediately.'
      ],
      nutrition: { calories: 420, protein: '32g', carbs: '48g', fat: '12g' }
    },
    '3': {
      id: '3',
      title: 'Beef Stir Fry with Vegetables',
      image: 'https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=1200&h=800&fit=crop',
      description: 'Tender beef strips with crisp vegetables in a savory Asian-inspired sauce. Perfect weeknight dinner.',
      prepTime: 15,
      cookTime: 10,
      servings: 4,
      difficulty: 'Easy',
      cuisine: 'Asian',
      ingredients: [
        { name: 'Beef sirloin', amount: '1 lb', available: true },
        { name: 'Broccoli', amount: '2 cups', available: true },
        { name: 'Carrot', amount: '2', available: true },
        { name: 'Soy sauce', amount: '3 tbsp', available: false },
        { name: 'Ginger', amount: '1 tbsp', available: false },
        { name: 'Garlic', amount: '3 cloves', available: true },
        { name: 'Sesame oil', amount: '1 tbsp', available: false }
      ],
      instructions: [
        'Slice beef thinly against the grain for tender pieces.',
        'Heat oil in a wok or large skillet over high heat until smoking.',
        'Stir-fry beef in batches until browned, about 2-3 minutes. Set aside.',
        'Add vegetables and stir-fry for 3-4 minutes until crisp-tender.',
        'Add garlic and ginger, cook for 30 seconds until fragrant.',
        'Return beef to the wok and add sauce.',
        'Toss everything together and drizzle with sesame oil.',
        'Serve immediately over steamed rice.'
      ],
      nutrition: { calories: 380, protein: '38g', carbs: '18g', fat: '16g' }
    },
    '4': {
      id: '4',
      title: 'Margherita Pizza',
      image: 'https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=1200&h=800&fit=crop',
      description: 'Classic Italian pizza with fresh mozzarella, ripe tomatoes, and fragrant basil on a crispy crust.',
      prepTime: 30,
      cookTime: 15,
      servings: 4,
      difficulty: 'Medium',
      cuisine: 'Italian',
      ingredients: [
        { name: 'Pizza dough', amount: '1 lb', available: false },
        { name: 'Tomato sauce', amount: '1 cup', available: true },
        { name: 'Fresh mozzarella', amount: '8 oz', available: false },
        { name: 'Fresh basil', amount: '1/2 cup', available: false },
        { name: 'Olive oil', amount: '2 tbsp', available: true },
        { name: 'Salt', amount: 'to taste', available: true }
      ],
      instructions: [
        'Preheat oven to 475°F (245°C) with a pizza stone if available.',
        'Roll out pizza dough on a floured surface to desired thickness.',
        'Transfer dough to a pizza peel or baking sheet.',
        'Spread tomato sauce evenly, leaving a border for the crust.',
        'Tear fresh mozzarella and distribute over the sauce.',
        'Drizzle with olive oil and season with salt.',
        'Bake for 12-15 minutes until crust is golden and cheese is bubbly.',
        'Top with fresh basil leaves immediately after removing from oven.'
      ],
      nutrition: { calories: 480, protein: '18g', carbs: '62g', fat: '18g' }
    },
    '5': {
      id: '5',
      title: 'Greek Salad with Grilled Chicken',
      image: 'https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=1200&h=800&fit=crop',
      description: 'Fresh Mediterranean salad topped with juicy grilled chicken. Light, healthy, and full of flavor.',
      prepTime: 15,
      cookTime: 10,
      servings: 4,
      difficulty: 'Easy',
      cuisine: 'Greek',
      ingredients: [
        { name: 'Chicken breast', amount: '1 lb', available: true },
        { name: 'Cucumber', amount: '2', available: true },
        { name: 'Tomatoes', amount: '3', available: true },
        { name: 'Feta cheese', amount: '6 oz', available: false },
        { name: 'Kalamata olives', amount: '1/2 cup', available: false },
        { name: 'Red onion', amount: '1', available: true },
        { name: 'Olive oil', amount: '1/4 cup', available: true },
        { name: 'Lemon', amount: '1', available: true }
      ],
      instructions: [
        'Season chicken breasts with salt, pepper, and dried oregano.',
        'Grill chicken for 5-6 minutes per side until internal temp reaches 165°F.',
        'Let chicken rest for 5 minutes, then slice into strips.',
        'Dice cucumber, quarter tomatoes, and slice red onion thinly.',
        'Combine vegetables in a large bowl with olives.',
        'Whisk together olive oil, lemon juice, and oregano for dressing.',
        'Add crumbled feta and top with sliced grilled chicken.',
        'Drizzle with dressing and serve immediately.'
      ],
      nutrition: { calories: 340, protein: '32g', carbs: '14g', fat: '18g' }
    },
    '6': {
      id: '6',
      title: 'Vegetable Curry',
      image: 'https://images.unsplash.com/photo-1455619452474-d2be8b1e70cd?w=1200&h=800&fit=crop',
      description: 'Aromatic Indian curry with mixed vegetables in a rich coconut milk sauce. Comforting and delicious.',
      prepTime: 20,
      cookTime: 30,
      servings: 6,
      difficulty: 'Medium',
      cuisine: 'Indian',
      ingredients: [
        { name: 'Potatoes', amount: '3', available: true },
        { name: 'Carrots', amount: '2', available: true },
        { name: 'Peas', amount: '1 cup', available: true },
        { name: 'Coconut milk', amount: '1 can', available: false },
        { name: 'Curry powder', amount: '3 tbsp', available: false },
        { name: 'Onion', amount: '1', available: true },
        { name: 'Garlic', amount: '4 cloves', available: true },
        { name: 'Ginger', amount: '2 tbsp', available: false }
      ],
      instructions: [
        'Dice potatoes and carrots into bite-sized cubes.',
        'Sauté diced onion until soft and translucent.',
        'Add minced garlic and ginger, cook until fragrant.',
        'Stir in curry powder and toast for 1 minute.',
        'Add vegetables and stir to coat with spices.',
        'Pour in coconut milk and bring to a simmer.',
        'Cook for 20-25 minutes until vegetables are tender.',
        'Season with salt and serve over basmati rice.'
      ],
      nutrition: { calories: 280, protein: '6g', carbs: '38g', fat: '12g' }
    },
    '7': {
      id: '7',
      title: 'Grilled Salmon with Lemon',
      image: 'https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=1200&h=800&fit=crop',
      description: 'Perfectly grilled salmon fillet with fresh lemon and aromatic dill. Healthy and elegant.',
      prepTime: 10,
      cookTime: 15,
      servings: 4,
      difficulty: 'Easy',
      cuisine: 'American',
      ingredients: [
        { name: 'Salmon fillets', amount: '4', available: true },
        { name: 'Lemon', amount: '2', available: true },
        { name: 'Fresh dill', amount: '1/4 cup', available: false },
        { name: 'Garlic', amount: '3 cloves', available: true },
        { name: 'Olive oil', amount: '3 tbsp', available: true },
        { name: 'Salt', amount: 'to taste', available: true },
        { name: 'Black pepper', amount: 'to taste', available: true }
      ],
      instructions: [
        'Preheat grill to medium-high heat (400°F).',
        'Mix olive oil, minced garlic, lemon zest, and chopped dill.',
        'Brush salmon fillets generously with the mixture.',
        'Season both sides with salt and pepper.',
        'Place salmon skin-side down on the grill.',
        'Grill for 6-8 minutes without moving.',
        'Flip carefully and grill another 4-6 minutes until done.',
        'Serve with lemon wedges and extra fresh dill.'
      ],
      nutrition: { calories: 320, protein: '34g', carbs: '2g', fat: '18g' }
    },
    '8': {
      id: '8',
      title: 'Mushroom Risotto',
      image: 'https://images.unsplash.com/photo-1476124369491-e7addf5db371?w=1200&h=800&fit=crop',
      description: 'Creamy Italian rice dish with earthy mushrooms and aged parmesan. Pure comfort food.',
      prepTime: 10,
      cookTime: 35,
      servings: 4,
      difficulty: 'Medium',
      cuisine: 'Italian',
      ingredients: [
        { name: 'Arborio rice', amount: '1.5 cups', available: false },
        { name: 'Mushrooms', amount: '12 oz', available: true },
        { name: 'Parmesan cheese', amount: '1 cup', available: true },
        { name: 'Butter', amount: '4 tbsp', available: true },
        { name: 'Onion', amount: '1', available: true },
        { name: 'Garlic', amount: '3 cloves', available: true },
        { name: 'White wine', amount: '1/2 cup', available: false },
        { name: 'Chicken broth', amount: '4 cups', available: false }
      ],
      instructions: [
        'Heat broth in a saucepan and keep warm over low heat.',
        'Sauté sliced mushrooms in butter until golden. Set aside.',
        'In the same pan, sauté diced onion until soft.',
        'Add rice and toast, stirring constantly, for 2 minutes.',
        'Add wine and stir until absorbed.',
        'Add warm broth one ladle at a time, stirring constantly.',
        'Continue until rice is creamy and al dente (about 20 minutes).',
        'Fold in mushrooms, butter, and parmesan. Season and serve.'
      ],
      nutrition: { calories: 420, protein: '14g', carbs: '58g', fat: '14g' }
    },
    '9': {
      id: '9',
      title: 'Tacos al Pastor',
      image: 'https://images.unsplash.com/photo-1565299585323-38d6b0865b47?w=1200&h=800&fit=crop',
      description: 'Authentic Mexican street tacos with marinated pork and caramelized pineapple.',
      prepTime: 20,
      cookTime: 15,
      servings: 6,
      difficulty: 'Medium',
      cuisine: 'Mexican',
      ingredients: [
        { name: 'Pork shoulder', amount: '2 lbs', available: true },
        { name: 'Pineapple', amount: '1 cup', available: true },
        { name: 'Onion', amount: '1', available: true },
        { name: 'Cilantro', amount: '1/2 cup', available: false },
        { name: 'Lime', amount: '2', available: true },
        { name: 'Corn tortillas', amount: '12', available: false },
        { name: 'Achiote paste', amount: '2 tbsp', available: false }
      ],
      instructions: [
        'Blend achiote paste with lime juice and spices for marinade.',
        'Coat pork slices in marinade and refrigerate for 2+ hours.',
        'Heat a cast iron skillet over high heat.',
        'Cook marinated pork until caramelized, about 3-4 min per side.',
        'Chop cooked pork into small pieces.',
        'Grill pineapple slices until charred, then dice.',
        'Warm tortillas on the grill or in a dry pan.',
        'Assemble tacos with pork, pineapple, onion, cilantro, and lime.'
      ],
      nutrition: { calories: 380, protein: '28g', carbs: '32g', fat: '16g' }
    },
    '10': {
      id: '10',
      title: 'Tom Yum Soup',
      image: 'https://images.unsplash.com/photo-1548943487-a2e4e43b4853?w=1200&h=800&fit=crop',
      description: 'Spicy and sour Thai soup with shrimp, mushrooms, and aromatic herbs. Bold and refreshing.',
      prepTime: 15,
      cookTime: 20,
      servings: 4,
      difficulty: 'Medium',
      cuisine: 'Thai',
      ingredients: [
        { name: 'Shrimp', amount: '1 lb', available: true },
        { name: 'Mushrooms', amount: '8 oz', available: true },
        { name: 'Lemongrass', amount: '3 stalks', available: false },
        { name: 'Lime', amount: '3', available: true },
        { name: 'Thai chilies', amount: '3-5', available: false },
        { name: 'Fish sauce', amount: '3 tbsp', available: false },
        { name: 'Galangal', amount: '1 inch', available: false },
        { name: 'Kaffir lime leaves', amount: '4', available: false }
      ],
      instructions: [
        'Bring 4 cups of water or broth to a boil.',
        'Bruise lemongrass and add with galangal and lime leaves.',
        'Simmer for 10 minutes to infuse aromatics.',
        'Add sliced mushrooms and cook for 3 minutes.',
        'Add shrimp and cook until pink (2-3 minutes).',
        'Remove from heat immediately.',
        'Stir in lime juice, fish sauce, and crushed chilies.',
        'Garnish with fresh cilantro and serve hot.'
      ],
      nutrition: { calories: 180, protein: '24g', carbs: '12g', fat: '4g' }
    }
  };

  private isBrowser: boolean;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    @Inject(PLATFORM_ID) platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(platformId);
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const id = params['id'];
      this.currentRecipeId = id;
      console.log('Recipe ID from route:', id);
      console.log('Available recipe IDs:', Object.keys(this.recipes));
      this.loadRecipe(id);
    });
  }

  loadRecipe(id: string): void {
    console.log('Loading recipe with ID:', id);
    this.recipe = this.recipes[id] || this.recipes['1'];
    console.log('Loaded recipe:', this.recipe);
    this.loading = false;
    this.checkIfSaved();
  }

  getMissingCount(): number {
    return this.recipe?.ingredients.filter(i => !i.available).length || 0;
  }

  checkIfSaved(): void {
    if (this.isBrowser && this.recipe) {
      const savedRecipesJson = localStorage.getItem('savedRecipes');
      if (savedRecipesJson) {
        const savedRecipes = JSON.parse(savedRecipesJson);
        this.isSaved.set(savedRecipes.some((r: any) => r.id === this.recipe!.id));
      }
    }
  }

  toggleSaveRecipe(): void {
    if (!this.recipe || !this.isBrowser) return;

    const savedRecipesJson = localStorage.getItem('savedRecipes');
    let savedRecipes = savedRecipesJson ? JSON.parse(savedRecipesJson) : [];

    if (this.isSaved()) {
      // Remove from saved
      savedRecipes = savedRecipes.filter((r: any) => r.id !== this.recipe!.id);
      localStorage.setItem('savedRecipes', JSON.stringify(savedRecipes));
      this.isSaved.set(false);
      console.log('Recipe removed from saved');
    } else {
      // Add to saved
      const recipeToSave = {
        id: this.recipe.id,
        name: this.recipe.title,
        cuisine: this.recipe.cuisine,
        time: `${this.recipe.prepTime + this.recipe.cookTime} min`,
        image: this.recipe.image
      };
      savedRecipes.push(recipeToSave);
      localStorage.setItem('savedRecipes', JSON.stringify(savedRecipes));
      this.isSaved.set(true);
      console.log('Recipe saved successfully!');
    }
  }

  findSubstitutions(): void {
    if (!this.recipe || !this.isBrowser || !this.http) return;

    const missingIngredients = this.recipe.ingredients
      .filter(i => !i.available)
      .map(i => i.name);

    if (missingIngredients.length === 0) {
      alert('No missing ingredients to substitute!');
      return;
    }

    console.log('🔍 Calling substitution API for:', missingIngredients);

    // Call the substitution API
    const requestBody = {
      recipeId: this.recipe.id,
      missingIngredients: missingIngredients,
      availableIngredients: this.recipe.ingredients
        .filter(i => i.available)
        .map(i => i.name),
      preferences: {
        dietaryRestrictions: []
      }
    };

    this.http.post<any>('http://localhost:8083/api/v1/substitutions/suggest', requestBody)
      .subscribe({
        next: (response) => {
          console.log('✅ Substitutions received:', response);
          this.showSubstitutionsDialog(response);
        },
        error: (error) => {
          console.error('❌ Error getting substitutions:', error);
          alert(`Error: ${error.message || 'Failed to get substitutions. Please try again.'}`);
        }
      });
  }

  private showSubstitutionsDialog(response: any): void {
    const substitutions = response.substitutions || [];
    
    let message = `Found ${substitutions.length} substitution(s):\n\n`;
    
    substitutions.forEach((sub: any) => {
      message += `\n🔸 ${sub.original}:\n`;
      sub.suggestions.forEach((suggestion: any, index: number) => {
        message += `  ${index + 1}. ${suggestion.substitute} (${suggestion.conversionRatio})\n`;
        message += `     ${suggestion.explanation}\n`;
      });
    });

    alert(message);
  }
}
