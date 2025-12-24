import { TestBed } from '@angular/core/testing';
import { OfflineStorageService } from './offline-storage.service';
import { db, Recipe } from './database.service';

describe('OfflineStorageService', () => {
  let service: OfflineStorageService;

  beforeEach(async () => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OfflineStorageService);
    
    await db.recipes.clear();
    await db.ingredients.clear();
    await db.preferences.clear();
    await db.pendingSync.clear();
  });

  afterEach(async () => {
    await db.recipes.clear();
    await db.ingredients.clear();
    await db.preferences.clear();
    await db.pendingSync.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('cacheRecipe', () => {
    it('should cache a recipe', (done) => {
      const recipe: Recipe = {
        id: '1',
        title: 'Test Recipe',
        image: 'test.jpg',
        ingredients: [],
        instructions: [],
        nutrition: { calories: 100, protein: '10g', carbs: '20g', fat: '5g' },
        cachedAt: new Date()
      };

      service.cacheRecipe(recipe).subscribe({
        next: (id) => {
          expect(id).toBe('1');
          done();
        },
        error: done.fail
      });
    });

    it('should update existing recipe', (done) => {
      const recipe: Recipe = {
        id: '1',
        title: 'Test Recipe',
        image: 'test.jpg',
        ingredients: [],
        instructions: [],
        nutrition: { calories: 100, protein: '10g', carbs: '20g', fat: '5g' },
        cachedAt: new Date()
      };

      service.cacheRecipe(recipe).subscribe(() => {
        const updatedRecipe = { ...recipe, title: 'Updated Recipe' };
        service.cacheRecipe(updatedRecipe).subscribe({
          next: () => {
            service.getCachedRecipe('1').subscribe({
              next: (cached) => {
                expect(cached?.title).toBe('Updated Recipe');
                done();
              },
              error: done.fail
            });
          },
          error: done.fail
        });
      });
    });
  });

  describe('getCachedRecipe', () => {
    it('should retrieve cached recipe', (done) => {
      const recipe: Recipe = {
        id: '1',
        title: 'Test Recipe',
        image: 'test.jpg',
        ingredients: [],
        instructions: [],
        nutrition: { calories: 100, protein: '10g', carbs: '20g', fat: '5g' },
        cachedAt: new Date()
      };

      service.cacheRecipe(recipe).subscribe(() => {
        service.getCachedRecipe('1').subscribe({
          next: (cached) => {
            expect(cached).toBeDefined();
            expect(cached?.title).toBe('Test Recipe');
            done();
          },
          error: done.fail
        });
      });
    });

    it('should return undefined for non-existent recipe', (done) => {
      service.getCachedRecipe('non-existent').subscribe({
        next: (cached) => {
          expect(cached).toBeUndefined();
          done();
        },
        error: done.fail
      });
    });
  });

  describe('saveIngredients', () => {
    it('should save ingredients to IndexedDB', (done) => {
      const ingredients = ['chicken', 'garlic', 'tomato'];

      service.saveIngredients(ingredients).subscribe({
        next: () => {
          service.getIngredients().subscribe({
            next: (saved) => {
              expect(saved).toEqual(ingredients);
              done();
            },
            error: done.fail
          });
        },
        error: done.fail
      });
    });

    it('should replace existing ingredients', (done) => {
      const ingredients1 = ['chicken', 'garlic'];
      const ingredients2 = ['beef', 'onion'];

      service.saveIngredients(ingredients1).subscribe(() => {
        service.saveIngredients(ingredients2).subscribe({
          next: () => {
            service.getIngredients().subscribe({
              next: (saved) => {
                expect(saved).toEqual(ingredients2);
                expect(saved).not.toContain('chicken');
                done();
              },
              error: done.fail
            });
          },
          error: done.fail
        });
      });
    });
  });

  describe('preferences', () => {
    it('should save and retrieve preference', (done) => {
      const key = 'theme';
      const value = 'dark';

      service.savePreference(key, value).subscribe(() => {
        service.getPreference<string>(key).subscribe({
          next: (saved) => {
            expect(saved).toBe(value);
            done();
          },
          error: done.fail
        });
      });
    });

    it('should return undefined for non-existent preference', (done) => {
      service.getPreference('non-existent').subscribe({
        next: (value) => {
          expect(value).toBeUndefined();
          done();
        },
        error: done.fail
      });
    });
  });

  describe('pending sync', () => {
    it('should queue pending sync operation', (done) => {
      service.queuePendingSync('create', 'ingredient', { name: 'test' }).subscribe({
        next: (id) => {
          expect(id).toBeGreaterThan(0);
          done();
        },
        error: done.fail
      });
    });

    it('should retrieve pending sync operations', (done) => {
      service.queuePendingSync('create', 'ingredient', { name: 'test1' }).subscribe(() => {
        service.queuePendingSync('update', 'recipe', { id: '1' }).subscribe(() => {
          service.getPendingSync().subscribe({
            next: (pending) => {
              expect(pending.length).toBe(2);
              expect(pending[0].operation).toBe('create');
              expect(pending[1].operation).toBe('update');
              done();
            },
            error: done.fail
          });
        });
      });
    });

    it('should clear pending sync operation', (done) => {
      service.queuePendingSync('create', 'ingredient', { name: 'test' }).subscribe((id) => {
        service.clearPendingSync(id).subscribe(() => {
          service.getPendingSync().subscribe({
            next: (pending) => {
              expect(pending.length).toBe(0);
              done();
            },
            error: done.fail
          });
        });
      });
    });
  });

  describe('clearAllCache', () => {
    it('should clear all cached data', (done) => {
      const recipe: Recipe = {
        id: '1',
        title: 'Test',
        image: 'test.jpg',
        ingredients: [],
        instructions: [],
        nutrition: { calories: 100, protein: '10g', carbs: '20g', fat: '5g' },
        cachedAt: new Date()
      };

      service.cacheRecipe(recipe).subscribe(() => {
        service.saveIngredients(['test']).subscribe(() => {
          service.clearAllCache().subscribe({
            next: () => {
              service.getCachedRecipes().subscribe({
                next: (recipes) => {
                  expect(recipes.length).toBe(0);
                  service.getIngredients().subscribe({
                    next: (ingredients) => {
                      expect(ingredients.length).toBe(0);
                      done();
                    },
                    error: done.fail
                  });
                },
                error: done.fail
              });
            },
            error: done.fail
          });
        });
      });
    });
  });
});
