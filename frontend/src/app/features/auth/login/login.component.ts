import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  template: `
    <div class="login-container">
      <mat-card class="login-card">
        <mat-card-header>
          <mat-card-title>
            <h1>Welcome Back</h1>
          </mat-card-title>
          <mat-card-subtitle>Sign in to Recipe Adjuster</mat-card-subtitle>
        </mat-card-header>
        
        <mat-card-content>
          <form [formGroup]="loginForm" (ngSubmit)="onSubmit()">
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Email</mat-label>
              <input matInput type="email" formControlName="email" placeholder="your@email.com">
              <mat-icon matPrefix>email</mat-icon>
              @if (loginForm.get('email')?.hasError('required') && loginForm.get('email')?.touched) {
                <mat-error>Email is required</mat-error>
              }
              @if (loginForm.get('email')?.hasError('email') && loginForm.get('email')?.touched) {
                <mat-error>Please enter a valid email</mat-error>
              }
            </mat-form-field>

            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Password</mat-label>
              <input matInput [type]="hidePassword() ? 'password' : 'text'" formControlName="password">
              <mat-icon matPrefix>lock</mat-icon>
              <button mat-icon-button matSuffix type="button" (click)="togglePasswordVisibility()">
                <mat-icon>{{ hidePassword() ? 'visibility_off' : 'visibility' }}</mat-icon>
              </button>
              @if (loginForm.get('password')?.hasError('required') && loginForm.get('password')?.touched) {
                <mat-error>Password is required</mat-error>
              }
            </mat-form-field>

            @if (errorMessage()) {
              <div class="error-message">
                <mat-icon>error</mat-icon>
                <span>{{ errorMessage() }}</span>
              </div>
            }

            <button mat-raised-button color="primary" type="submit" class="full-width login-btn" 
                    [disabled]="loginForm.invalid || loading()">
              @if (loading()) {
                <mat-spinner diameter="20"></mat-spinner>
              } @else {
                Login
              }
            </button>
          </form>

          <div class="demo-credentials">
            <p><strong>Demo Credentials:</strong></p>
            <p>Email: demo@recipeadjuster.com</p>
            <p>Password: demo123</p>
            <button mat-stroked-button color="primary" type="button" class="full-width guest-btn" 
                    (click)="loginAsGuest()">
              <mat-icon>person</mat-icon>
              Continue as Guest
            </button>
          </div>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .login-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: calc(100vh - 200px);
      padding: 2rem;
    }

    .login-card {
      max-width: 450px;
      width: 100%;
    }

    mat-card-header {
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;
      margin-bottom: 2rem;
    }

    h1 {
      margin: 0;
      font-size: 2rem;
      font-weight: 600;
      color: #333;
    }

    mat-card-subtitle {
      margin-top: 0.5rem;
      font-size: 1rem;
      color: #666;
    }

    form {
      display: flex;
      flex-direction: column;
      gap: 1rem;
    }

    .full-width {
      width: 100%;
    }

    .login-btn {
      margin-top: 1rem;
      height: 48px;
      font-size: 1rem;
      font-weight: 500;
    }

    .error-message {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.75rem;
      background-color: #ffebee;
      color: #c62828;
      border-radius: 4px;
      font-size: 0.875rem;

      mat-icon {
        font-size: 20px;
        width: 20px;
        height: 20px;
      }
    }

    .demo-credentials {
      margin-top: 2rem;
      padding: 1rem;
      background-color: #e3f2fd;
      border-radius: 8px;
      text-align: center;

      p {
        margin: 0.25rem 0;
        font-size: 0.875rem;
        color: #1565c0;
      }

      strong {
        color: #0d47a1;
      }
    }

    mat-spinner {
      display: inline-block;
      margin: 0 auto;
    }

    .guest-btn {
      margin-top: 1rem;
      height: 44px;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 0.5rem;
      
      mat-icon {
        margin: 0;
        font-size: 20px;
        width: 20px;
        height: 20px;
      }
    }
  `]
})
export class LoginComponent {
  loginForm: FormGroup;
  hidePassword = signal(true);
  loading = signal(false);
  errorMessage = signal<string | null>(null);

  constructor(
    private fb: FormBuilder,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  togglePasswordVisibility(): void {
    this.hidePassword.set(!this.hidePassword());
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.loading.set(true);
      this.errorMessage.set(null);

      const { email, password } = this.loginForm.value;

      setTimeout(() => {
        if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
          // Get all registered users
          const registeredUsersJson = localStorage.getItem('registeredUsers');
          const registeredUsers = registeredUsersJson ? JSON.parse(registeredUsersJson) : [];
          
          // Check if user exists with matching email and password
          const foundUser = registeredUsers.find((u: any) => 
            u.email === email && u.password === password
          );
          
          // Also check demo account
          const isDemoAccount = email === 'demo@recipeadjuster.com' && password === 'demo123';
          
          if (foundUser || isDemoAccount) {
            const user = foundUser || {
              id: '1',
              email: email,
              name: 'Demo User',
              firstName: 'Demo',
              lastName: 'User'
            };
            
            // Remove password from stored user data
            const { password: _, ...userWithoutPassword } = user;
            
            localStorage.setItem('user', JSON.stringify(userWithoutPassword));
            localStorage.setItem('token', 'auth-token-' + Date.now());
            
            this.loading.set(false);
            this.router.navigate(['/home']);
          } else {
            this.loading.set(false);
            this.errorMessage.set('Invalid email or password. Please check your credentials.');
          }
        }
      }, 1000);
    }
  }

  loginAsGuest(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    setTimeout(() => {
      if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
        const guestUser = {
          id: 'guest-' + Date.now(),
          email: 'guest@recipeadjuster.com',
          name: 'Guest User',
          firstName: 'Guest',
          lastName: 'User'
        };
        
        localStorage.setItem('user', JSON.stringify(guestUser));
        localStorage.setItem('token', 'guest-token-' + Date.now());
        
        this.loading.set(false);
        this.router.navigate(['/home']);
      }
    }, 500);
  }
}
