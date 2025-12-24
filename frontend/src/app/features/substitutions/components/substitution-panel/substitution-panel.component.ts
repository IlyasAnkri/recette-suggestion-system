import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AiSubstitutionService, SubstituteOption } from '../../services/ai-substitution.service';

interface Substitution {
  ingredient: string;
  substitutes: SubstituteOption[];
  loading?: boolean;
}

@Component({
  selector: 'app-substitution-panel',
  standalone: true,
  imports: [
    CommonModule,
    MatSidenavModule,
    MatButtonModule,
    MatIconModule,
    MatListModule,
    MatChipsModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './substitution-panel.component.html',
  styleUrls: ['./substitution-panel.component.scss']
})
export class SubstitutionPanelComponent implements OnChanges {
  @Input() opened = false;
  @Input() missingIngredients: string[] = [];
  @Output() closed = new EventEmitter<void>();
  @Output() substitutionSelected = new EventEmitter<{ ingredient: string; substitute: string }>();

  substitutions: Substitution[] = [];

  constructor(private aiService: AiSubstitutionService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['missingIngredients'] && this.missingIngredients.length > 0) {
      this.loadSubstitutions();
    }
  }

  loadSubstitutions(): void {
    this.substitutions = this.missingIngredients.map(ingredient => ({
      ingredient,
      substitutes: [],
      loading: true
    }));

    this.missingIngredients.forEach((ingredient, index) => {
      this.aiService.getSubstitutions(ingredient).subscribe({
        next: (substitutes) => {
          this.substitutions[index].substitutes = substitutes;
          this.substitutions[index].loading = false;
        },
        error: (error) => {
          console.error(`Error loading substitutions for ${ingredient}:`, error);
          this.substitutions[index].loading = false;
        }
      });
    });
  }

  onClose(): void {
    this.closed.emit();
  }

  useSubstitution(ingredient: string, substitute: string): void {
    this.substitutionSelected.emit({ ingredient, substitute });
  }
}
