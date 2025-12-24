import { createAction, props } from '@ngrx/store';

export const addIngredient = createAction(
  '[Ingredient] Add Ingredient',
  props<{ ingredient: string }>()
);

export const removeIngredient = createAction(
  '[Ingredient] Remove Ingredient',
  props<{ ingredient: string }>()
);

export const loadSuggestions = createAction(
  '[Ingredient] Load Suggestions',
  props<{ query: string }>()
);

export const loadSuggestionsSuccess = createAction(
  '[Ingredient] Load Suggestions Success',
  props<{ suggestions: string[] }>()
);

export const loadSuggestionsFailure = createAction(
  '[Ingredient] Load Suggestions Failure',
  props<{ error: string }>()
);

export const searchRecipes = createAction(
  '[Ingredient] Search Recipes',
  props<{ ingredients: string[] }>()
);

export const saveToStorage = createAction(
  '[Ingredient] Save To Storage',
  props<{ ingredients: string[] }>()
);

export const loadFromStorage = createAction(
  '[Ingredient] Load From Storage'
);

export const loadFromStorageSuccess = createAction(
  '[Ingredient] Load From Storage Success',
  props<{ ingredients: string[] }>()
);
