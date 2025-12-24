import { ActionReducerMap } from '@ngrx/store';
import { ingredientReducer, IngredientState } from '../features/home/store/ingredient.reducer';

export interface AppState {
  ingredient: IngredientState;
}

export const reducers: ActionReducerMap<AppState> = {
  ingredient: ingredientReducer
};
