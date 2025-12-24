import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';

interface NavItem {
  label: string;
  route: string;
  icon: string;
}

@Component({
  selector: 'app-nav',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    RouterLinkActive,
    MatListModule,
    MatIconModule
  ],
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.scss']
})
export class NavComponent {
  navItems: NavItem[] = [
    { label: 'Home', route: '/home', icon: 'home' },
    { label: 'Search', route: '/search', icon: 'search' },
    { label: 'Saved Recipes', route: '/saved', icon: 'bookmark' },
    { label: 'Profile', route: '/profile', icon: 'person' }
  ];
}
