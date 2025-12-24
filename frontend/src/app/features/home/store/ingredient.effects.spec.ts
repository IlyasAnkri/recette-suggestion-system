import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { Observable, of, throwError } from 'rxjs';
import { IngredientEffects } from './ingredient.effects';
import { IngredientService } from '../services/ingredient.service';
import * as IngredientActions from './ingredient.actions';
import { Action } from '@ngrx/store';

describe('IngredientEffects', () => {
  let actions$: Observable<Action>;
  let effects: IngredientEffects;
  let ingredientService: jasmine.SpyObj<IngredientService>;

  beforeEach(() => {
    const serviceSpy = jasmine.createSpyObj('IngredientService', ['getSuggestions']);

    TestBed.configureTestingModule({
      providers: [
        IngredientEffects,
        provideMockActions(() => actions$),
        { provide: IngredientService, useValue: serviceSpy }
      ]
    });

    effects = TestBed.inject(IngredientEffects);
    ingredientService = TestBed.inject(IngredientService) as jasmine.SpyObj<IngredientService>;
  });

  describe('loadSuggestions$', () => {
    it('should return loadSuggestionsSuccess on successful API call', (done) => {
      const query = 'tom';
      const suggestions = ['tomato', 'tomato sauce', 'tomato paste'];
      const action = IngredientActions.loadSuggestions({ query });
      const outcome = IngredientActions.loadSuggestionsSuccess({ suggestions });

      actions$ = of(action);
      ingredientService.getSuggestions.and.returnValue(of(suggestions));

      effects.loadSuggestions$.subscribe((result) => {
        expect(result).toEqual(outcome);
        expect(ingredientService.getSuggestions).toHaveBeenCalledWith(query);
        done();
      });
    });

    it('should return loadSuggestionsFailure on API error', (done) => {
      const query = 'tom';
      const error = new Error('Network error');
      const action = IngredientActions.loadSuggestions({ query });
      const outcome = IngredientActions.loadSuggestionsFailure({ error: error.message });

      actions$ = of(action);
      ingredientService.getSuggestions.and.returnValue(throwError(() => error));

      effects.loadSuggestions$.subscribe((result) => {
        expect(result).toEqual(outcome);
        done();
      });
    });
  });

  describe('saveToStorage$', () => {
    it('should save ingredients to localStorage', (done) => {
      const ingredients = ['chicken', 'garlic'];
      const action = IngredientActions.saveToStorage({ ingredients });
      
      spyOn(localStorage, 'setItem');
      actions$ = of(action);

      effects.saveToStorage$.subscribe(() => {
        expect(localStorage.setItem).toHaveBeenCalledWith(
          'ingredients',
          JSON.stringify(ingredients)
        );
        done();
      });
    });
  });

  describe('loadFromStorage$', () => {
    it('should load ingredients from localStorage', (done) => {
      const ingredients = ['chicken', 'garlic'];
      const action = IngredientActions.loadFromStorage();
      const outcome = IngredientActions.loadFromStorageSuccess({ ingredients });

      spyOn(localStorage, 'getItem').and.returnValue(JSON.stringify(ingredients));
      actions$ = of(action);

      effects.loadFromStorage$.subscribe((result) => {
        expect(result).toEqual(outcome);
        expect(localStorage.getItem).toHaveBeenCalledWith('ingredients');
        done();
      });
    });

    it('should return empty array when localStorage is empty', (done) => {
      const action = IngredientActions.loadFromStorage();
      const outcome = IngredientActions.loadFromStorageSuccess({ ingredients: [] });

      spyOn(localStorage, 'getItem').and.returnValue(null);
      actions$ = of(action);

      effects.loadFromStorage$.subscribe((result) => {
        expect(result).toEqual(outcome);
        done();
      });
    });

    it('should handle JSON parse errors gracefully', (done) => {
      const action = IngredientActions.loadFromStorage();
      const outcome = IngredientActions.loadFromStorageSuccess({ ingredients: [] });

      spyOn(localStorage, 'getItem').and.returnValue('invalid json');
      actions$ = of(action);

      effects.loadFromStorage$.subscribe((result) => {
        expect(result).toEqual(outcome);
        done();
      });
    });
  });
});
