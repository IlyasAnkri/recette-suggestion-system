import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';

interface SubstitutionDialogData {
  substitutions: any[];
}

@Component({
  selector: 'app-substitution-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule, MatIconModule, MatChipsModule],
  template: `
    <h2 mat-dialog-title>
      <mat-icon>swap_horiz</mat-icon>
      Ingredient Substitutions
    </h2>
    
    <mat-dialog-content>
      <div class="substitutions-container">
        <div *ngFor="let sub of data.substitutions" class="substitution-group">
          <h3 class="original-ingredient">
            <mat-icon>cancel</mat-icon>
            {{ sub.original }}
          </h3>
          
          <div *ngIf="sub.suggestions && sub.suggestions.length > 0" class="suggestions-list">
            <div *ngFor="let suggestion of sub.suggestions; let i = index" class="suggestion-card">
              <div class="suggestion-header">
                <span class="suggestion-number">{{ i + 1 }}</span>
                <h4 class="substitute-name">{{ suggestion.substitute }}</h4>
                <mat-chip class="ratio-chip">{{ suggestion.conversionRatio }}</mat-chip>
              </div>
              <p class="explanation">{{ suggestion.explanation }}</p>
            </div>
          </div>
          
          <div *ngIf="!sub.suggestions || sub.suggestions.length === 0" class="no-suggestions">
            <mat-icon>info</mat-icon>
            <p>No substitutions available for this ingredient</p>
          </div>
        </div>
      </div>
    </mat-dialog-content>
    
    <mat-dialog-actions align="end">
      <button mat-raised-button color="primary" (click)="close()">
        <mat-icon>check</mat-icon>
        Got it
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    h2[mat-dialog-title] {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      color: #667eea;
      margin: 0;
      padding: 1.5rem;
      border-bottom: 2px solid #f0f0f0;
      
      mat-icon {
        font-size: 28px;
        width: 28px;
        height: 28px;
      }
    }
    
    mat-dialog-content {
      padding: 1.5rem;
      max-height: 70vh;
      overflow-y: auto;
    }
    
    .substitutions-container {
      display: flex;
      flex-direction: column;
      gap: 2rem;
    }
    
    .substitution-group {
      background: #f8f9fa;
      border-radius: 12px;
      padding: 1.5rem;
    }
    
    .original-ingredient {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      color: #e53935;
      font-size: 1.1rem;
      font-weight: 600;
      margin: 0 0 1rem 0;
      
      mat-icon {
        color: #e53935;
      }
    }
    
    .suggestions-list {
      display: flex;
      flex-direction: column;
      gap: 1rem;
    }
    
    .suggestion-card {
      background: white;
      border-radius: 8px;
      padding: 1rem;
      border-left: 4px solid #667eea;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
      transition: transform 0.2s ease, box-shadow 0.2s ease;
      
      &:hover {
        transform: translateX(4px);
        box-shadow: 0 4px 8px rgba(0,0,0,0.15);
      }
    }
    
    .suggestion-header {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      margin-bottom: 0.5rem;
    }
    
    .suggestion-number {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 24px;
      height: 24px;
      background: #667eea;
      color: white;
      border-radius: 50%;
      font-size: 0.85rem;
      font-weight: 600;
      flex-shrink: 0;
    }
    
    .substitute-name {
      flex: 1;
      margin: 0;
      font-size: 1rem;
      font-weight: 600;
      color: #333;
    }
    
    .ratio-chip {
      background: #e3f2fd !important;
      color: #1976d2 !important;
      font-weight: 600;
      font-size: 0.85rem;
    }
    
    .explanation {
      margin: 0;
      color: #666;
      font-size: 0.9rem;
      line-height: 1.5;
      padding-left: 32px;
    }
    
    .no-suggestions {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      padding: 1rem;
      background: #fff3cd;
      border-radius: 8px;
      color: #856404;
      
      mat-icon {
        color: #856404;
      }
      
      p {
        margin: 0;
      }
    }
    
    mat-dialog-actions {
      padding: 1rem 1.5rem;
      border-top: 2px solid #f0f0f0;
      
      button {
        display: flex;
        align-items: center;
        gap: 0.5rem;
      }
    }
  `]
})
export class SubstitutionDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<SubstitutionDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SubstitutionDialogData
  ) {}
  
  close(): void {
    this.dialogRef.close();
  }
}
