import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCheckboxModule } from '@angular/material/checkbox';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatCheckboxModule
  ],
  template: `
    <div class="register-container">
      <mat-card class="register-card">
        <mat-card-header>
          <mat-card-title>
            <h1>Create Account</h1>
          </mat-card-title>
          <mat-card-subtitle>Join Recipe Adjuster today</mat-card-subtitle>
        </mat-card-header>
        
        <mat-card-content>
          <form [formGroup]="registerForm" (ngSubmit)="onSubmit()">
            <div class="name-row">
              <mat-form-field appearance="outline" class="half-width">
                <mat-label>First Name</mat-label>
                <input matInput formControlName="firstName" placeholder="John">
                <mat-icon matPrefix>person</mat-icon>
                @if (registerForm.get('firstName')?.hasError('required') && registerForm.get('firstName')?.touched) {
                  <mat-error>First name is required</mat-error>
                }
              </mat-form-field>

              <mat-form-field appearance="outline" class="half-width">
                <mat-label>Last Name</mat-label>
                <input matInput formControlName="lastName" placeholder="Doe">
                @if (registerForm.get('lastName')?.hasError('required') && registerForm.get('lastName')?.touched) {
                  <mat-error>Last name is required</mat-error>
                }
              </mat-form-field>
            </div>

            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Email</mat-label>
              <input matInput type="email" formControlName="email" placeholder="your@email.com">
              <mat-icon matPrefix>email</mat-icon>
              @if (registerForm.get('email')?.hasError('required') && registerForm.get('email')?.touched) {
                <mat-error>Email is required</mat-error>
              }
              @if (registerForm.get('email')?.hasError('email') && registerForm.get('email')?.touched) {
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
              @if (registerForm.get('password')?.hasError('required') && registerForm.get('password')?.touched) {
                <mat-error>Password is required</mat-error>
              }
              @if (registerForm.get('password')?.hasError('minlength') && registerForm.get('password')?.touched) {
                <mat-error>Password must be at least 8 characters</mat-error>
              }
            </mat-form-field>

            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Confirm Password</mat-label>
              <input matInput [type]="hideConfirmPassword() ? 'password' : 'text'" formControlName="confirmPassword">
              <mat-icon matPrefix>lock_outline</mat-icon>
              <button mat-icon-button matSuffix type="button" (click)="toggleConfirmPasswordVisibility()">
                <mat-icon>{{ hideConfirmPassword() ? 'visibility_off' : 'visibility' }}</mat-icon>
              </button>
              @if (registerForm.get('confirmPassword')?.hasError('required') && registerForm.get('confirmPassword')?.touched) {
                <mat-error>Please confirm your password</mat-error>
              }
            </mat-form-field>

            @if (passwordMismatch()) {
              <div class="error-message">
                <mat-icon>error</mat-icon>
                <span>Passwords do not match</span>
              </div>
            }

            @if (errorMessage()) {
              <div class="error-message">
                <mat-icon>error</mat-icon>
                <span>{{ errorMessage() }}</span>
              </div>
            }

            <mat-checkbox formControlName="acceptTerms" class="terms-checkbox">
              I agree to the <a routerLink="/terms">Terms of Service</a> and <a routerLink="/privacy">Privacy Policy</a>
            </mat-checkbox>

            <button mat-raised-button color="primary" type="submit" class="full-width register-btn" 
                    [disabled]="registerForm.invalid || loading() || passwordMismatch()">
              @if (loading()) {
                <mat-spinner diameter="20"></mat-spinner>
              } @else {
                Create Account
              }
            </button>
          </form>

          <div class="login-link">
            <p>Already have an account? <a routerLink="/auth/login">Sign in</a></p>
          </div>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .register-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: calc(100vh - 200px);
      padding: 2rem;
    }

    .register-card {
      max-width: 500px;
      width: 100%;
    }

    mat-card-header {
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;
      margin-bottom: 1.5rem;
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
      gap: 0.75rem;
    }

    .name-row {
      display: flex;
      gap: 1rem;
    }

    .half-width {
      flex: 1;
    }

    .full-width {
      width: 100%;
    }

    .register-btn {
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

    .terms-checkbox {
      margin: 0.5rem 0;
      
      a {
        color: #1976d2;
        text-decoration: none;
        
        &:hover {
          text-decoration: underline;
        }
      }
    }

    .login-link {
      margin-top: 1.5rem;
      text-align: center;

      p {
        margin: 0;
        color: #666;
      }

      a {
        color: #1976d2;
        text-decoration: none;
        font-weight: 500;

        &:hover {
          text-decoration: underline;
        }
      }
    }

    mat-spinner {
      display: inline-block;
      margin: 0 auto;
    }

    @media (max-width: 480px) {
      .name-row {
        flex-direction: column;
        gap: 0.75rem;
      }
    }
  `]
})
export class RegisterComponent {
  registerForm: FormGroup;
  hidePassword = signal(true);
  hideConfirmPassword = signal(true);
  loading = signal(false);
  errorMessage = signal<string | null>(null);

  constructor(
    private fb: FormBuilder,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]],
      acceptTerms: [false, [Validators.requiredTrue]]
    });
  }

  togglePasswordVisibility(): void {
    this.hidePassword.set(!this.hidePassword());
  }

  toggleConfirmPasswordVisibility(): void {
    this.hideConfirmPassword.set(!this.hideConfirmPassword());
  }

  passwordMismatch(): boolean {
    const password = this.registerForm.get('password')?.value;
    const confirmPassword = this.registerForm.get('confirmPassword')?.value;
    return confirmPassword && password !== confirmPassword;
  }

  onSubmit(): void {
    if (this.registerForm.valid && !this.passwordMismatch()) {
      this.loading.set(true);
      this.errorMessage.set(null);

      const { firstName, lastName, email, password } = this.registerForm.value;

      setTimeout(() => {
        if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
          // Check if user already exists
          const registeredUsersJson = localStorage.getItem('registeredUsers');
          const registeredUsers = registeredUsersJson ? JSON.parse(registeredUsersJson) : [];
          
          const userExists = registeredUsers.some((u: any) => u.email === email);
          
          if (userExists) {
            this.loading.set(false);
            this.errorMessage.set('An account with this email already exists.');
            return;
          }
          
          // Create new user
          const newUser = {
            id: Date.now().toString(),
            email: email,
            name: `${firstName} ${lastName}`,
            firstName,
            lastName,
            password: password // Store password for demo purposes (in production, never do this!)
          };
          
          // Add to registered users list
          registeredUsers.push(newUser);
          localStorage.setItem('registeredUsers', JSON.stringify(registeredUsers));
          
          // Log user in immediately
          const { password: _, ...userWithoutPassword } = newUser;
          localStorage.setItem('user', JSON.stringify(userWithoutPassword));
          localStorage.setItem('token', 'auth-token-' + Date.now());
        }
        
        this.loading.set(false);
        this.router.navigate(['/home']);
      }, 1500);
    }
  }
}
