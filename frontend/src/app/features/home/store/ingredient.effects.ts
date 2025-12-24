import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { map, catchError, switchMap, debounceTime } from 'rxjs/operators';
import * as IngredientActions from './ingredient.actions';
import { IngredientService } from '../services/ingredient.service';

@Injectable()
export class IngredientEffects {
  private actions$ = inject(Actions);
  private ingredientService = inject(IngredientService);

  loadSuggestions$ = createEffect(() =>
    this.actions$.pipe(
      ofType(IngredientActions.loadSuggestions),
      debounceTime(300),
      switchMap(({ query }) =>
        this.ingredientService.getSuggestions(query).pipe(
          map(suggestions => IngredientActions.loadSuggestionsSuccess({ suggestions })),
          catchError(error => of(IngredientActions.loadSuggestionsFailure({ error: error.message })))
        )
      )
    )
  );

  saveToStorage$ = createEffect(() =>
    this.actions$.pipe(
      ofType(IngredientActions.saveToStorage),
      map(({ ingredients }) => {
        if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
          localStorage.setItem('ingredients', JSON.stringify(ingredients));
        }
        return { type: '[Ingredient] Save To Storage Success' };
      })
    )
  );

  loadFromStorage$ = createEffect(() =>
    this.actions$.pipe(
      ofType(IngredientActions.loadFromStorage),
      map(() => {
        let ingredients: string[] = [];
        if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
          const stored = localStorage.getItem('ingredients');
          ingredients = stored ? JSON.parse(stored) : [];
        }
        return IngredientActions.loadFromStorageSuccess({ ingredients });
      })
    )
  );
}
