import { selectIngredients, selectSuggestions, selectLoading, selectError } from './ingredient.selectors';
import { IngredientState } from './ingredient.reducer';

describe('Ingredient Selectors', () => {
  const mockState = {
    ingredient: {
      ingredients: ['chicken', 'garlic', 'tomato'],
      suggestions: ['tomato', 'tomato sauce'],
      loading: false,
      error: null
    } as IngredientState
  };

  describe('selectIngredients', () => {
    it('should select ingredients array', () => {
      const result = selectIngredients.projector(mockState.ingredient);
      expect(result).toEqual(['chicken', 'garlic', 'tomato']);
    });

    it('should return empty array when no ingredients', () => {
      const emptyState = { ...mockState.ingredient, ingredients: [] };
      const result = selectIngredients.projector(emptyState);
      expect(result).toEqual([]);
    });
  });

  describe('selectSuggestions', () => {
    it('should select suggestions array', () => {
      const result = selectSuggestions.projector(mockState.ingredient);
      expect(result).toEqual(['tomato', 'tomato sauce']);
    });

    it('should return empty array when no suggestions', () => {
      const emptyState = { ...mockState.ingredient, suggestions: [] };
      const result = selectSuggestions.projector(emptyState);
      expect(result).toEqual([]);
    });
  });

  describe('selectLoading', () => {
    it('should select loading state as false', () => {
      const result = selectLoading.projector(mockState.ingredient);
      expect(result).toBe(false);
    });

    it('should select loading state as true', () => {
      const loadingState = { ...mockState.ingredient, loading: true };
      const result = selectLoading.projector(loadingState);
      expect(result).toBe(true);
    });
  });

  describe('selectError', () => {
    it('should select null error', () => {
      const result = selectError.projector(mockState.ingredient);
      expect(result).toBeNull();
    });

    it('should select error message', () => {
      const errorState = { ...mockState.ingredient, error: 'Network error' };
      const result = selectError.projector(errorState);
      expect(result).toBe('Network error');
    });
  });
});
