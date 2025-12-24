import { createFeatureSelector, createSelector } from '@ngrx/store';
import { IngredientState } from './ingredient.reducer';

export const selectIngredientState = createFeatureSelector<IngredientState>('ingredient');

export const selectIngredients = createSelector(
  selectIngredientState,
  (state) => state.submittedIngredients
);

export const selectSuggestions = createSelector(
  selectIngredientState,
  (state) => state.suggestions
);

export const selectLoading = createSelector(
  selectIngredientState,
  (state) => state.loading
);

export const selectError = createSelector(
  selectIngredientState,
  (state) => state.error
);
