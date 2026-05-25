import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink],
  template: `
    <div class="container">
      <div class="header">
        <h1>User Management</h1>
        <span class="spacer"></span>
        <a routerLink="/users" class="btn">Users</a>
        <a routerLink="/users/new" class="btn primary">New User</a>
      </div>
      <router-outlet />
    </div>
  `
})
export class AppComponent {}
