import { Injectable } from '@angular/core';
import { db, Recipe, Ingredient, UserPreference, PendingSync } from './database.service';
import { Observable, from, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class OfflineStorageService {
  private readonly MAX_CACHE_SIZE = 100;
  private readonly CACHE_EXPIRY_DAYS = 7;

  cacheRecipe(recipe: Recipe): Observable<string> {
    return from(
      db.transaction('rw', db.recipes, async () => {
        const cachedRecipe = {
          ...recipe,
          cachedAt: new Date()
        };
        
        await this.enforceStorageLimit();
        await db.recipes.put(cachedRecipe);
        return recipe.id;
      })
    ).pipe(
      catchError(error => {
        console.error('Error caching recipe:', error);
        throw error;
      })
    );
  }

  getCachedRecipe(id: string): Observable<Recipe | undefined> {
    return from(db.recipes.get(id)).pipe(
      map(recipe => {
        if (recipe && this.isRecipeValid(recipe)) {
          return recipe;
        }
        return undefined;
      }),
      catchError(error => {
        console.error('Error retrieving cached recipe:', error);
        return of(undefined);
      })
    );
  }

  getCachedRecipes(): Observable<Recipe[]> {
    return from(
      db.recipes
        .orderBy('cachedAt')
        .reverse()
        .limit(this.MAX_CACHE_SIZE)
        .toArray()
    ).pipe(
      map(recipes => recipes.filter((r: Recipe) => this.isRecipeValid(r))),
      catchError(error => {
        console.error('Error retrieving cached recipes:', error);
        return of([]);
      })
    );
  }

  clearExpiredRecipes(): Observable<number> {
    const expiryDate = new Date();
    expiryDate.setDate(expiryDate.getDate() - this.CACHE_EXPIRY_DAYS);

    return from(
      db.recipes
        .where('cachedAt')
        .below(expiryDate)
        .delete()
    ).pipe(
      catchError(error => {
        console.error('Error clearing expired recipes:', error);
        return of(0);
      })
    );
  }

  saveIngredients(ingredients: string[]): Observable<void> {
    return from(
      db.transaction('rw', db.ingredients, async () => {
        await db.ingredients.clear();
        const ingredientRecords = ingredients.map(name => ({
          name,
          addedAt: new Date()
        }));
        await db.ingredients.bulkAdd(ingredientRecords);
      })
    ).pipe(
      catchError(error => {
        console.error('Error saving ingredients:', error);
        throw error;
      })
    );
  }

  getIngredients(): Observable<string[]> {
    return from(
      db.ingredients
        .orderBy('addedAt')
        .toArray()
    ).pipe(
      map(ingredients => ingredients.map(i => i.name)),
      catchError(error => {
        console.error('Error retrieving ingredients:', error);
        return of([]);
      })
    );
  }

  savePreference(key: string, value: any): Observable<void> {
    return from(
      db.preferences.put({
        key,
        value,
        updatedAt: new Date()
      })
    ).pipe(
      map(() => undefined),
      catchError(error => {
        console.error('Error saving preference:', error);
        throw error;
      })
    );
  }

  getPreference<T>(key: string): Observable<T | undefined> {
    return from(db.preferences.get(key)).pipe(
      map(pref => pref?.value as T | undefined),
      catchError(error => {
        console.error('Error retrieving preference:', error);
        return of(undefined);
      })
    );
  }

  queuePendingSync(operation: PendingSync['operation'], entity: PendingSync['entity'], data: any): Observable<number> {
    return from(
      db.pendingSync.add({
        operation,
        entity,
        data,
        timestamp: new Date(),
        retryCount: 0
      })
    ).pipe(
      catchError(error => {
        console.error('Error queuing pending sync:', error);
        throw error;
      })
    );
  }

  getPendingSync(): Observable<PendingSync[]> {
    return from(
      db.pendingSync
        .orderBy('timestamp')
        .toArray()
    ).pipe(
      catchError(error => {
        console.error('Error retrieving pending sync:', error);
        return of([]);
      })
    );
  }

  clearPendingSync(id: number): Observable<void> {
    return from(db.pendingSync.delete(id)).pipe(
      map(() => undefined),
      catchError(error => {
        console.error('Error clearing pending sync:', error);
        throw error;
      })
    );
  }

  incrementSyncRetry(id: number): Observable<void> {
    return from(
      db.transaction('rw', db.pendingSync, async () => {
        const item = await db.pendingSync.get(id);
        if (item) {
          await db.pendingSync.update(id, { retryCount: item.retryCount + 1 });
        }
      })
    ).pipe(
      catchError(error => {
        console.error('Error incrementing sync retry:', error);
        throw error;
      })
    );
  }

  clearAllCache(): Observable<void> {
    return from(
      db.transaction('rw', [db.recipes, db.ingredients, db.preferences, db.pendingSync], async () => {
        await db.recipes.clear();
        await db.ingredients.clear();
        await db.preferences.clear();
        await db.pendingSync.clear();
      })
    ).pipe(
      map(() => undefined),
      catchError(error => {
        console.error('Error clearing cache:', error);
        throw error;
      })
    );
  }

  private async enforceStorageLimit(): Promise<void> {
    const count = await db.recipes.count();
    if (count >= this.MAX_CACHE_SIZE) {
      const oldestRecipes = await db.recipes
        .orderBy('cachedAt')
        .limit(Math.floor(this.MAX_CACHE_SIZE * 0.2))
        .toArray();
      
      const idsToDelete = oldestRecipes.map((r: Recipe) => r.id);
      await db.recipes.bulkDelete(idsToDelete);
    }
  }

  private isRecipeValid(recipe: Recipe): boolean {
    const expiryDate = new Date();
    expiryDate.setDate(expiryDate.getDate() - this.CACHE_EXPIRY_DAYS);
    return recipe.cachedAt >= expiryDate;
  }
}
