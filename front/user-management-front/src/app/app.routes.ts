import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'users' },
  {
    path: 'users',
    loadComponent: () => import('./features/users/pages/users-list/users-list.component').then(m => m.UsersListComponent)
  },
  {
    path: 'users/new',
    loadComponent: () => import('./features/users/pages/user-edit/user-edit.component').then(m => m.UserEditComponent)
  },
  {
    path: 'users/:id/edit',
    loadComponent: () => import('./features/users/pages/user-edit/user-edit.component').then(m => m.UserEditComponent)
  },
  { path: '**', redirectTo: 'users' }
];
