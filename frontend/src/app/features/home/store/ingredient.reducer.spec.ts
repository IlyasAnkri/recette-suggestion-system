import { ingredientReducer, initialState } from './ingredient.reducer';
import * as IngredientActions from './ingredient.actions';

describe('IngredientReducer', () => {
  describe('unknown action', () => {
    it('should return the default state', () => {
      const action = { type: 'Unknown' };
      const state = ingredientReducer(initialState, action as any);

      expect(state).toBe(initialState);
    });
  });

  describe('addIngredient', () => {
    it('should add ingredient to empty state', () => {
      const action = IngredientActions.addIngredient({ ingredient: 'tomato' });
      const state = ingredientReducer(initialState, action);

      expect(state.ingredients).toEqual(['tomato']);
    });

    it('should add ingredient to existing list', () => {
      const currentState = { ...initialState, ingredients: ['chicken', 'garlic'] };
      const action = IngredientActions.addIngredient({ ingredient: 'tomato' });
      const state = ingredientReducer(currentState, action);

      expect(state.ingredients).toEqual(['chicken', 'garlic', 'tomato']);
    });

    it('should not add duplicate ingredient', () => {
      const currentState = { ...initialState, ingredients: ['chicken', 'garlic'] };
      const action = IngredientActions.addIngredient({ ingredient: 'chicken' });
      const state = ingredientReducer(currentState, action);

      expect(state.ingredients).toEqual(['chicken', 'garlic']);
    });
  });

  describe('removeIngredient', () => {
    it('should remove ingredient from state', () => {
      const currentState = { ...initialState, ingredients: ['chicken', 'garlic', 'tomato'] };
      const action = IngredientActions.removeIngredient({ ingredient: 'garlic' });
      const state = ingredientReducer(currentState, action);

      expect(state.ingredients).toEqual(['chicken', 'tomato']);
    });

    it('should preserve order when removing middle ingredient', () => {
      const currentState = { ...initialState, ingredients: ['a', 'b', 'c', 'd'] };
      const action = IngredientActions.removeIngredient({ ingredient: 'b' });
      const state = ingredientReducer(currentState, action);

      expect(state.ingredients).toEqual(['a', 'c', 'd']);
    });

    it('should handle removing non-existent ingredient', () => {
      const currentState = { ...initialState, ingredients: ['chicken', 'garlic'] };
      const action = IngredientActions.removeIngredient({ ingredient: 'beef' });
      const state = ingredientReducer(currentState, action);

      expect(state.ingredients).toEqual(['chicken', 'garlic']);
    });
  });

  describe('loadSuggestions', () => {
    it('should set loading to true', () => {
      const action = IngredientActions.loadSuggestions({ query: 'tom' });
      const state = ingredientReducer(initialState, action);

      expect(state.loading).toBe(true);
      expect(state.error).toBeNull();
    });
  });

  describe('loadSuggestionsSuccess', () => {
    it('should update suggestions and set loading to false', () => {
      const currentState = { ...initialState, loading: true };
      const suggestions = ['tomato', 'tomato sauce', 'tomato paste'];
      const action = IngredientActions.loadSuggestionsSuccess({ suggestions });
      const state = ingredientReducer(currentState, action);

      expect(state.suggestions).toEqual(suggestions);
      expect(state.loading).toBe(false);
      expect(state.error).toBeNull();
    });
  });

  describe('loadSuggestionsFailure', () => {
    it('should set error and loading to false', () => {
      const currentState = { ...initialState, loading: true };
      const error = 'Network error';
      const action = IngredientActions.loadSuggestionsFailure({ error });
      const state = ingredientReducer(currentState, action);

      expect(state.error).toBe(error);
      expect(state.loading).toBe(false);
      expect(state.suggestions).toEqual([]);
    });
  });

  describe('clearIngredients', () => {
    it('should clear all ingredients', () => {
      const currentState = { ...initialState, ingredients: ['chicken', 'garlic', 'tomato'] };
      const action = IngredientActions.clearIngredients();
      const state = ingredientReducer(currentState, action);

      expect(state.ingredients).toEqual([]);
    });
  });

  describe('loadFromStorageSuccess', () => {
    it('should load ingredients from storage', () => {
      const ingredients = ['chicken', 'garlic'];
      const action = IngredientActions.loadFromStorageSuccess({ ingredients });
      const state = ingredientReducer(initialState, action);

      expect(state.ingredients).toEqual(ingredients);
    });
  });
});
