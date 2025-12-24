import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class IngredientService {
  private apiUrl = `${environment.apiBaseUrl}/ingredients`;

  private mockIngredients = [
    'chicken', 'beef', 'pork', 'fish', 'shrimp', 'tofu',
    'rice', 'pasta', 'bread', 'flour', 'eggs', 'milk',
    'cheese', 'butter', 'olive oil', 'garlic', 'onion',
    'tomato', 'potato', 'carrot', 'broccoli', 'spinach',
    'bell pepper', 'mushroom', 'zucchini', 'cucumber',
    'lettuce', 'basil', 'oregano', 'thyme', 'rosemary',
    'salt', 'pepper', 'sugar', 'honey', 'soy sauce',
    'vinegar', 'lemon', 'lime', 'ginger', 'chili'
  ];

  constructor(private http: HttpClient) {}

  getSuggestions(query: string): Observable<string[]> {
    const filtered = this.mockIngredients
      .filter(ingredient => ingredient.toLowerCase().includes(query.toLowerCase()))
      .slice(0, 10);
    
    return of(filtered).pipe(delay(200));
  }
}
