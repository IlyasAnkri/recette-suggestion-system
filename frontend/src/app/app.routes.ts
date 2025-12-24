import { Routes } from '@angular/router';
import { PreloadAllModules } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },
  {
    path: 'home',
    loadChildren: () => import('./features/home/home.routes').then(m => m.HOME_ROUTES)
  },
  {
    path: 'search',
    loadChildren: () => import('./features/recipe-search/recipe-search.routes').then(m => m.RECIPE_SEARCH_ROUTES),
    data: { preload: true }
  },
  {
    path: 'recipes/:id',
    loadChildren: () => import('./features/recipe-detail/recipe-detail.routes').then(m => m.RECIPE_DETAIL_ROUTES),
    data: { preload: true }
  },
  {
    path: 'substitutions',
    loadChildren: () => import('./features/substitutions/substitutions.routes').then(m => m.SUBSTITUTIONS_ROUTES)
  },
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  {
    path: 'profile',
    loadChildren: () => import('./features/profile/profile.routes').then(m => m.PROFILE_ROUTES)
  },
  {
    path: '**',
    redirectTo: 'home'
  }
];
