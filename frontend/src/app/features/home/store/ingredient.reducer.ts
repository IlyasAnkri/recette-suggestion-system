import { createReducer, on } from '@ngrx/store';
import * as IngredientActions from './ingredient.actions';

export interface IngredientState {
  submittedIngredients: string[];
  suggestions: string[];
  loading: boolean;
  error: string | null;
}

export const initialState: IngredientState = {
  submittedIngredients: [],
  suggestions: [],
  loading: false,
  error: null
};

export const ingredientReducer = createReducer(
  initialState,
  on(IngredientActions.addIngredient, (state, { ingredient }) => ({
    ...state,
    submittedIngredients: [...state.submittedIngredients, ingredient]
  })),
  on(IngredientActions.removeIngredient, (state, { ingredient }) => ({
    ...state,
    submittedIngredients: state.submittedIngredients.filter(i => i !== ingredient)
  })),
  on(IngredientActions.loadSuggestions, (state) => ({
    ...state,
    loading: true,
    error: null
  })),
  on(IngredientActions.loadSuggestionsSuccess, (state, { suggestions }) => ({
    ...state,
    suggestions,
    loading: false
  })),
  on(IngredientActions.loadSuggestionsFailure, (state, { error }) => ({
    ...state,
    error,
    loading: false,
    suggestions: []
  })),
  on(IngredientActions.loadFromStorageSuccess, (state, { ingredients }) => ({
    ...state,
    submittedIngredients: ingredients
  }))
);
