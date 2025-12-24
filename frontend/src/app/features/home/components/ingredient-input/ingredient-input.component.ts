import { Component, signal, computed, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import * as IngredientActions from '../../store/ingredient.actions';
import { selectIngredients, selectSuggestions, selectLoading } from '../../store/ingredient.selectors';

@Component({
  selector: 'app-ingredient-input',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatChipsModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatAutocompleteModule
  ],
  templateUrl: './ingredient-input.component.html',
  styleUrls: ['./ingredient-input.component.scss']
})
export class IngredientInputComponent {
  ingredientControl = new FormControl('');
  ingredients = signal<string[]>([]);
  
  suggestions$: Observable<string[]>;
  loading$: Observable<boolean>;
  
  validationError = computed(() => {
    const count = this.ingredients().length;
    if (count === 0) return 'Add at least 1 ingredient';
    if (count > 20) return 'Maximum 20 ingredients allowed';
    return null;
  });
  
  canSearch = computed(() => {
    const count = this.ingredients().length;
    return count >= 1 && count <= 20;
  });

  constructor(
    private store: Store,
    private router: Router
  ) {
    this.suggestions$ = this.store.select(selectSuggestions);
    this.loading$ = this.store.select(selectLoading);
    
    this.store.select(selectIngredients).subscribe(ingredients => {
      this.ingredients.set(ingredients);
    });

    this.ingredientControl.valueChanges.subscribe(value => {
      if (value && value.length >= 2) {
        this.store.dispatch(IngredientActions.loadSuggestions({ query: value }));
      }
    });

    effect(() => {
      const ingredients = this.ingredients();
      if (ingredients.length > 0) {
        this.store.dispatch(IngredientActions.saveToStorage({ ingredients }));
      }
    });
  }

  addIngredient(event: any): void {
    const value = (event.value || '').trim().toLowerCase();
    
    if (value && !this.ingredients().includes(value)) {
      if (this.ingredients().length < 20) {
        this.store.dispatch(IngredientActions.addIngredient({ ingredient: value }));
        this.ingredientControl.setValue('');
      }
    }
  }

  removeIngredient(ingredient: string): void {
    this.store.dispatch(IngredientActions.removeIngredient({ ingredient }));
  }

  selectSuggestion(suggestion: string): void {
    if (!this.ingredients().includes(suggestion) && this.ingredients().length < 20) {
      this.store.dispatch(IngredientActions.addIngredient({ ingredient: suggestion }));
      this.ingredientControl.setValue('');
    }
  }

  searchRecipes(): void {
    if (this.canSearch()) {
      this.store.dispatch(IngredientActions.searchRecipes({ ingredients: this.ingredients() }));
      this.router.navigate(['/search']);
    }
  }

  browseAllRecipes(): void {
    // Clear ingredients to show all recipes
    if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
      localStorage.setItem('ingredients', JSON.stringify([]));
    }
    this.router.navigate(['/search']);
  }
}
