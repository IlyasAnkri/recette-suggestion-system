import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatChipsModule } from '@angular/material/chips';
import { MatSelectModule } from '@angular/material/select';
import { MatTabsModule } from '@angular/material/tabs';
import { MatListModule } from '@angular/material/list';
import { MatDividerModule } from '@angular/material/divider';

interface UserProfile {
  name: string;
  email: string;
  dietaryPreferences: string[];
  allergies: string[];
  cuisinePreferences: string[];
  savedRecipes: number;
  createdAt: string;
}

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatChipsModule,
    MatSelectModule,
    MatTabsModule,
    MatListModule,
    MatDividerModule
  ],
  template: `
    <div class="profile-container">
      <div class="profile-header">
        <div class="profile-banner">
          <div class="profile-info-overlay">
            <h1>{{ profile().name }}</h1>
            <p class="email">{{ profile().email }}</p>
            <p class="member-since">Member since {{ profile().createdAt }}</p>
          </div>
        </div>
      </div>

      <div class="profile-content">
        <mat-tab-group>
          <mat-tab label="Preferences">
            <div class="tab-content">
              <mat-card class="preference-card">
                <mat-card-header>
                  <mat-icon mat-card-avatar>restaurant</mat-icon>
                  <mat-card-title>Dietary Preferences</mat-card-title>
                  <mat-card-subtitle>Your dietary choices</mat-card-subtitle>
                </mat-card-header>
                <mat-card-content>
                  <div class="chips-container">
                    @for (pref of profile().dietaryPreferences; track pref) {
                      <mat-chip highlighted>{{ pref }}</mat-chip>
                    }
                    <button mat-icon-button class="add-chip-btn" (click)="addPreference()">
                      <mat-icon>add</mat-icon>
                    </button>
                  </div>
                </mat-card-content>
              </mat-card>

              <mat-card class="preference-card">
                <mat-card-header>
                  <mat-icon mat-card-avatar color="warn">warning</mat-icon>
                  <mat-card-title>Allergies & Restrictions</mat-card-title>
                  <mat-card-subtitle>Ingredients to avoid</mat-card-subtitle>
                </mat-card-header>
                <mat-card-content>
                  <div class="chips-container">
                    @for (allergy of profile().allergies; track allergy) {
                      <mat-chip color="warn" highlighted>{{ allergy }}</mat-chip>
                    }
                    <button mat-icon-button class="add-chip-btn" (click)="addAllergy()">
                      <mat-icon>add</mat-icon>
                    </button>
                  </div>
                </mat-card-content>
              </mat-card>

              <mat-card class="preference-card">
                <mat-card-header>
                  <mat-icon mat-card-avatar>public</mat-icon>
                  <mat-card-title>Favorite Cuisines</mat-card-title>
                  <mat-card-subtitle>Cuisines you enjoy</mat-card-subtitle>
                </mat-card-header>
                <mat-card-content>
                  <div class="chips-container">
                    @for (cuisine of profile().cuisinePreferences; track cuisine) {
                      <mat-chip color="accent" highlighted>{{ cuisine }}</mat-chip>
                    }
                    <button mat-icon-button class="add-chip-btn" (click)="addCuisine()">
                      <mat-icon>add</mat-icon>
                    </button>
                  </div>
                </mat-card-content>
              </mat-card>
            </div>
          </mat-tab>

          <mat-tab label="Saved Recipes">
            <div class="tab-content">
              <mat-card class="stats-card">
                <mat-card-content>
                  <div class="stats-grid">
                    <div class="stat-item">
                      <mat-icon>bookmark</mat-icon>
                      <span class="stat-number">{{ profile().savedRecipes }}</span>
                      <span class="stat-label">Saved Recipes</span>
                    </div>
                    <div class="stat-item">
                      <mat-icon>history</mat-icon>
                      <span class="stat-number">24</span>
                      <span class="stat-label">Recently Viewed</span>
                    </div>
                    <div class="stat-item">
                      <mat-icon>thumb_up</mat-icon>
                      <span class="stat-number">18</span>
                      <span class="stat-label">Liked Recipes</span>
                    </div>
                  </div>
                </mat-card-content>
              </mat-card>

              <mat-card>
                <mat-card-header>
                  <mat-card-title>Your Saved Recipes</mat-card-title>
                </mat-card-header>
                <mat-card-content>
                  <mat-list>
                    @for (recipe of savedRecipesList; track recipe.id) {
                      <mat-list-item>
                        <img matListItemAvatar [src]="recipe.image" [alt]="recipe.name">
                        <span matListItemTitle>{{ recipe.name }}</span>
                        <span matListItemLine>{{ recipe.cuisine }} - {{ recipe.time }}</span>
                      </mat-list-item>
                      <mat-divider></mat-divider>
                    }
                  </mat-list>
                </mat-card-content>
              </mat-card>
            </div>
          </mat-tab>

          <mat-tab label="Settings">
            <div class="tab-content">
              <mat-card>
                <mat-card-header>
                  <mat-card-title>Account Settings</mat-card-title>
                </mat-card-header>
                <mat-card-content>
                  <form class="settings-form">
                    <mat-form-field appearance="outline">
                      <mat-label>Display Name</mat-label>
                      <input matInput [value]="profile().name">
                    </mat-form-field>

                    <mat-form-field appearance="outline">
                      <mat-label>Email</mat-label>
                      <input matInput [value]="profile().email" type="email">
                    </mat-form-field>

                    <mat-form-field appearance="outline">
                      <mat-label>Preferred Language</mat-label>
                      <mat-select value="en">
                        <mat-option value="en">English</mat-option>
                        <mat-option value="fr">French</mat-option>
                        <mat-option value="es">Spanish</mat-option>
                      </mat-select>
                    </mat-form-field>

                    <button mat-raised-button color="primary">Save Changes</button>
                  </form>
                </mat-card-content>
              </mat-card>
            </div>
          </mat-tab>
        </mat-tab-group>
      </div>
    </div>
  `,
  styles: [`
    .profile-container {
      max-width: 900px;
      margin: 0 auto;
      padding-bottom: 2rem;
    }

    .profile-header {
      position: relative;
      margin-bottom: 2rem;
    }

    .profile-banner {
      height: 200px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
      border-radius: 20px;
      position: relative;
      display: flex;
      align-items: center;
      justify-content: center;
      overflow: hidden;
      box-shadow: 0 10px 40px rgba(102, 126, 234, 0.3);

      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: url('data:image/svg+xml,<svg width="100" height="100" xmlns="http://www.w3.org/2000/svg"><circle cx="50" cy="50" r="40" fill="rgba(255,255,255,0.05)"/></svg>');
        opacity: 0.3;
      }
    }

    .profile-info-overlay {
      position: relative;
      z-index: 1;
      text-align: center;
      color: white;
      padding: 2rem;

      h1 {
        margin: 0 0 0.75rem 0;
        font-size: 2.5rem;
        font-weight: 700;
        text-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
        letter-spacing: -0.5px;
      }

      .email {
        margin: 0.5rem 0;
        font-size: 1.1rem;
        opacity: 0.95;
        font-weight: 500;
        text-shadow: 0 1px 4px rgba(0, 0, 0, 0.2);
      }

      .member-since {
        margin: 0.5rem 0 0 0;
        font-size: 0.95rem;
        opacity: 0.85;
        text-shadow: 0 1px 4px rgba(0, 0, 0, 0.2);
      }
    }

    .profile-content {
      padding: 0 24px;
    }

    .tab-content {
      padding: 24px 0;
      display: flex;
      flex-direction: column;
      gap: 16px;
    }

    .preference-card {
      border-radius: 16px;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
      transition: transform 0.2s ease, box-shadow 0.2s ease;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
      }

      mat-card-header {
        margin-bottom: 16px;
      }

      mat-icon[mat-card-avatar] {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 12px;
        padding: 10px;
        color: white;
        font-size: 24px;
        width: 44px;
        height: 44px;
      }
    }

    .chips-container {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      align-items: center;
    }

    .add-chip-btn {
      border: 2px dashed #ccc;
      border-radius: 50%;
    }

    .stats-card {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
    }

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 24px;
      padding: 16px;
    }

    .stat-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;

      mat-icon {
        font-size: 32px;
        width: 32px;
        height: 32px;
        margin-bottom: 8px;
        opacity: 0.9;
      }

      .stat-number {
        font-size: 2rem;
        font-weight: 700;
      }

      .stat-label {
        font-size: 0.85rem;
        opacity: 0.9;
      }
    }

    .settings-form {
      display: flex;
      flex-direction: column;
      gap: 16px;
      max-width: 400px;
      padding-top: 16px;
    }

    @media (max-width: 600px) {
      .profile-avatar-section {
        flex-direction: column;
        align-items: center;
        left: 50%;
        transform: translateX(-50%);
        text-align: center;
      }

      .stats-grid {
        grid-template-columns: 1fr;
      }
    }
  `]
})
export class ProfileComponent implements OnInit {
  profile = signal<UserProfile>({
    name: 'Guest User',
    email: 'guest@recipeadjuster.com',
    dietaryPreferences: ['Vegetarian-Friendly', 'Low-Carb', 'High-Protein'],
    allergies: ['Peanuts', 'Shellfish'],
    cuisinePreferences: ['Italian', 'Asian', 'Mexican', 'Mediterranean'],
    savedRecipes: 12,
    createdAt: 'January 2025'
  });

  savedRecipesList: any[] = [];

  ngOnInit(): void {
    if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
      // First, get the current logged-in user
      const currentUserJson = localStorage.getItem('user');
      if (currentUserJson) {
        const currentUser = JSON.parse(currentUserJson);
        
        // Load saved recipes from localStorage
        const savedRecipesJson = localStorage.getItem('savedRecipes');
        if (savedRecipesJson) {
          this.savedRecipesList = JSON.parse(savedRecipesJson);
        }
        
        // Try to load saved profile preferences
        const savedProfile = localStorage.getItem('userProfile');
        if (savedProfile) {
          const profileData = JSON.parse(savedProfile);
          // Merge with current user data and update saved recipes count
          this.profile.set({
            ...profileData,
            name: currentUser.name || currentUser.email,
            email: currentUser.email,
            savedRecipes: this.savedRecipesList.length
          });
        } else {
          // Create default profile from user data
          this.profile.set({
            name: currentUser.name || currentUser.email,
            email: currentUser.email,
            dietaryPreferences: [],
            allergies: [],
            cuisinePreferences: [],
            savedRecipes: this.savedRecipesList.length,
            createdAt: new Date().toLocaleDateString('en-US', { month: 'long', year: 'numeric' })
          });
        }
      }
    }
  }

  addPreference(): void {
    const pref = prompt('Add dietary preference:');
    if (pref) {
      const current = this.profile();
      this.profile.set({
        ...current,
        dietaryPreferences: [...current.dietaryPreferences, pref]
      });
      this.saveProfile();
    }
  }

  addAllergy(): void {
    const allergy = prompt('Add allergy or restriction:');
    if (allergy) {
      const current = this.profile();
      this.profile.set({
        ...current,
        allergies: [...current.allergies, allergy]
      });
      this.saveProfile();
    }
  }

  addCuisine(): void {
    const cuisine = prompt('Add favorite cuisine:');
    if (cuisine) {
      const current = this.profile();
      this.profile.set({
        ...current,
        cuisinePreferences: [...current.cuisinePreferences, cuisine]
      });
      this.saveProfile();
    }
  }

  private saveProfile(): void {
    localStorage.setItem('userProfile', JSON.stringify(this.profile()));
  }
}
