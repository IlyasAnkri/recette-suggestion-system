import Dexie, { Table } from 'dexie';

export interface Recipe {
  id: string;
  title: string;
  image: string;
  ingredients: { name: string; amount: string; available: boolean }[];
  instructions: string[];
  nutrition: { calories: number; protein: string; carbs: string; fat: string };
  cuisine?: string;
  difficulty?: string;
  prepTime?: number;
  cookTime?: number;
  servings?: number;
  cachedAt: Date;
}

export interface Ingredient {
  id?: number;
  name: string;
  addedAt: Date;
}

export interface UserPreference {
  key: string;
  value: any;
  updatedAt: Date;
}

export interface PendingSync {
  id?: number;
  operation: 'create' | 'update' | 'delete';
  entity: 'recipe' | 'ingredient' | 'preference';
  data: any;
  timestamp: Date;
  retryCount: number;
}

export class AppDatabase extends Dexie {
  recipes!: Table<Recipe, string>;
  ingredients!: Table<Ingredient, number>;
  preferences!: Table<UserPreference, string>;
  pendingSync!: Table<PendingSync, number>;

  constructor() {
    super('RecipeAdjusterDB');
    
    this.version(1).stores({
      recipes: 'id, title, cuisine, cachedAt',
      ingredients: '++id, name, addedAt',
      preferences: 'key, updatedAt',
      pendingSync: '++id, operation, entity, timestamp'
    });
  }
}

export const db = new AppDatabase();
