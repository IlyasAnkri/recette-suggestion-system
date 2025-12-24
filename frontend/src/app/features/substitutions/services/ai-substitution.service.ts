import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

export interface SubstituteOption {
  name: string;
  ratio: string;
  explanation: string;
}

@Injectable({
  providedIn: 'root'
})
export class AiSubstitutionService {
  private apiUrl = 'http://localhost:8080/api/v1/substitutions/suggest';

  constructor(private http: HttpClient) {}

  getSubstitutions(ingredient: string): Observable<SubstituteOption[]> {
    const requestBody = {
      recipeId: 'temp-recipe-id',
      missingIngredients: [ingredient],
      availableIngredients: [],
      preferences: {
        dietaryRestrictions: []
      }
    };

    return this.http.post<any>(this.apiUrl, requestBody).pipe(
      map(response => {
        try {
          // Extract substitutions from backend response
          if (response.substitutions && response.substitutions.length > 0) {
            const firstSubstitution = response.substitutions[0];
            if (firstSubstitution.suggestions) {
              return firstSubstitution.suggestions.map((sug: any) => ({
                name: sug.substitute || sug.name,
                ratio: sug.ratio || '1:1',
                explanation: sug.explanation || sug.notes || ''
              })).slice(0, 3);
            }
          }
          return this.getFallbackSubstitutions(ingredient);
        } catch (error) {
          console.error('Error parsing backend response:', error);
          return this.getFallbackSubstitutions(ingredient);
        }
      }),
      catchError(error => {
        console.error('Error calling backend API:', error);
        return of(this.getFallbackSubstitutions(ingredient));
      })
    );
  }

  private getFallbackSubstitutions(ingredient: string): SubstituteOption[] {
    // Fallback substitutions database
    const fallbacks: { [key: string]: SubstituteOption[] } = {
      'marinara sauce': [
        { name: 'Crushed tomatoes + herbs', ratio: '1:1', explanation: 'Similar flavor, add basil and oregano' },
        { name: 'Tomato paste + water', ratio: '1:3', explanation: 'More concentrated, dilute as needed' },
        { name: 'Ketchup', ratio: '1:1', explanation: 'Sweeter alternative, reduce sugar in recipe' }
      ],
      'italian seasoning': [
        { name: 'Basil + oregano', ratio: '1:1', explanation: 'Classic Italian herb combination' },
        { name: 'Herbes de Provence', ratio: '1:1', explanation: 'Similar Mediterranean flavor profile' },
        { name: 'Thyme + rosemary', ratio: '1:1', explanation: 'Earthy alternative' }
      ],
      'fresh dill': [
        { name: 'Dried dill', ratio: '1:3', explanation: 'Use 1/3 the amount of dried' },
        { name: 'Fresh tarragon', ratio: '1:1', explanation: 'Similar anise-like flavor' },
        { name: 'Fresh parsley + fennel seeds', ratio: '1:1', explanation: 'Mimics dill flavor' }
      ]
    };

    const key = ingredient.toLowerCase();
    return fallbacks[key] || [
      { name: 'Similar ingredient', ratio: '1:1', explanation: 'Use a similar ingredient from the same category' },
      { name: 'Omit if optional', ratio: 'N/A', explanation: 'Can be omitted if not essential to the recipe' },
      { name: 'Ask for suggestions', ratio: 'N/A', explanation: 'Consult a recipe forum or cooking community' }
    ];
  }
}
