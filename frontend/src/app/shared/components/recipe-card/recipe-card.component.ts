import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

export interface Recipe {
  id: string;
  title: string;
  thumbnail: string;
  matchPercentage: number;
  missingIngredientsCount: number;
  cuisine?: string;
  difficulty?: string;
  prepTime?: number;
  ingredients?: string[];
}

@Component({
  selector: 'app-recipe-card',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatChipsModule,
    MatIconModule,
    MatButtonModule
  ],
  templateUrl: './recipe-card.component.html',
  styleUrls: ['./recipe-card.component.scss']
})
export class RecipeCardComponent {
  @Input() recipe!: Recipe;
  @Output() cardClick = new EventEmitter<string>();

  onCardClick(): void {
    this.cardClick.emit(this.recipe.id);
  }

  getMatchColor(): string {
    if (this.recipe.matchPercentage >= 80) return 'success';
    if (this.recipe.matchPercentage >= 60) return 'warning';
    return 'error';
  }
}
