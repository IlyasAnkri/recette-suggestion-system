import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Store } from '@ngrx/store';
import { IngredientInputComponent } from './components/ingredient-input/ingredient-input.component';
import * as IngredientActions from './store/ingredient.actions';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, IngredientInputComponent],
  template: `
    <div class="hero-section">
      <div class="hero-content">
        <div class="hero-badge">AI-Powered Recipe Finder</div>
        <h1>Discover Delicious Recipes</h1>
        <p class="hero-subtitle">
          Tell us what ingredients you have, and we'll find the perfect recipes for you. 
          Get smart substitution suggestions when you're missing something.
        </p>
      </div>
    </div>
    
    <div class="search-section">
      <app-ingredient-input></app-ingredient-input>
    </div>
    
    <div class="features-section">
      <h2>Why Recipe Adjuster?</h2>
      <div class="features-grid">
        <div class="feature-card">
          <div class="feature-icon">
            <span class="material-icons">search</span>
          </div>
          <h3>Smart Matching</h3>
          <p>Find recipes that match your available ingredients with our intelligent algorithm.</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">
            <span class="material-icons">swap_horiz</span>
          </div>
          <h3>Ingredient Substitutes</h3>
          <p>Missing an ingredient? Get AI-powered suggestions for perfect substitutions.</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">
            <span class="material-icons">favorite</span>
          </div>
          <h3>Save Favorites</h3>
          <p>Create your personal cookbook by saving recipes you love.</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">
            <span class="material-icons">restaurant_menu</span>
          </div>
          <h3>Dietary Preferences</h3>
          <p>Filter recipes based on your dietary needs and restrictions.</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .hero-section {
      background: linear-gradient(135deg, #1976d2 0%, #42a5f5 50%, #64b5f6 100%);
      padding: 4rem 2rem;
      text-align: center;
      color: white;
      margin: -20px -20px 0 -20px;
    }
    
    .hero-content {
      max-width: 800px;
      margin: 0 auto;
    }
    
    .hero-badge {
      display: inline-block;
      background: rgba(255,255,255,0.2);
      padding: 8px 20px;
      border-radius: 20px;
      font-size: 0.875rem;
      font-weight: 500;
      margin-bottom: 1.5rem;
      backdrop-filter: blur(10px);
    }
    
    h1 {
      font-size: 3rem;
      font-weight: 700;
      margin: 0 0 1rem 0;
      line-height: 1.2;
    }
    
    .hero-subtitle {
      font-size: 1.25rem;
      opacity: 0.95;
      margin: 0;
      line-height: 1.6;
    }
    
    .search-section {
      max-width: 700px;
      margin: -40px auto 3rem;
      padding: 0 1rem;
      position: relative;
      z-index: 10;
    }
    
    :host ::ng-deep .search-section .ingredient-input-container {
      background: white;
      border-radius: 16px;
      box-shadow: 0 10px 40px rgba(0,0,0,0.15);
      padding: 2rem;
    }
    
    .features-section {
      max-width: 1200px;
      margin: 0 auto;
      padding: 3rem 2rem 4rem;
      
      h2 {
        text-align: center;
        font-size: 2rem;
        color: #333;
        margin-bottom: 3rem;
        font-weight: 600;
      }
    }
    
    .features-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 2rem;
    }
    
    .feature-card {
      background: white;
      border-radius: 16px;
      padding: 2rem;
      text-align: center;
      box-shadow: 0 4px 20px rgba(0,0,0,0.08);
      transition: transform 0.3s ease, box-shadow 0.3s ease;
      
      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 8px 30px rgba(0,0,0,0.12);
      }
    }
    
    .feature-icon {
      width: 70px;
      height: 70px;
      background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 1.5rem;
      
      .material-icons {
        font-size: 32px;
        color: #1976d2;
      }
    }
    
    .feature-card h3 {
      font-size: 1.25rem;
      color: #333;
      margin: 0 0 0.75rem 0;
      font-weight: 600;
    }
    
    .feature-card p {
      font-size: 0.95rem;
      color: #666;
      margin: 0;
      line-height: 1.6;
    }
    
    @media (max-width: 768px) {
      .hero-section {
        padding: 3rem 1.5rem;
      }
      
      h1 {
        font-size: 2rem;
      }
      
      .hero-subtitle {
        font-size: 1rem;
      }
      
      .features-grid {
        gap: 1.5rem;
      }
    }
  `]
})
export class HomeComponent implements OnInit {
  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(IngredientActions.loadFromStorage());
  }
}
