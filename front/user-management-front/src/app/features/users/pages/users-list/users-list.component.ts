import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { UsersService } from '../../services/users.service';
import { PageResponse, User } from '../../models/user.model';
import { SearchBarComponent } from '../../../../shared/components/search-bar/search-bar.component';
import { PaginationComponent } from '../../../../shared/components/pagination/pagination.component';

@Component({
  selector: 'app-users-list',
  standalone: true,
  imports: [RouterLink, SearchBarComponent, PaginationComponent],
  templateUrl: './users-list.component.html',
  styleUrl: './users-list.component.css'
})
export class UsersListComponent {
  loading = false;
  error: string | null = null;

  search = '';
  page = 0;
  size = 10;

  data: PageResponse<User> = { content: [], page: 0, size: 10, totalElements: 0, totalPages: 0, first: true, last: true };

  constructor(private users: UsersService) {
    void this.load();
  }

  async load(): Promise<void> {
    this.loading = true;
    this.error = null;

    try {
      this.data = await this.users.list({ search: this.search, page: this.page, size: this.size, sortBy: 'id', direction: 'ASC' });
    } catch (e: any) {
      this.error = e?.message ?? 'Failed to load users';
    } finally {
      this.loading = false;
    }
  }

  async onSearch(term: string): Promise<void> {
    this.search = term;
    this.page = 0;
    await this.load();
  }

  async onPageChange(nextPage: number): Promise<void> {
    this.page = nextPage;
    await this.load();
  }

  async onDelete(user: User): Promise<void> {
    this.loading = true;
    this.error = null;

    try {
      await this.users.delete(user.id);
      if (this.data.content.length === 1 && this.page > 0) {
        this.page -= 1;
      }
      await this.load();
    } catch (e: any) {
      this.error = e?.message ?? 'Failed to delete user';
      this.loading = false;
    }
  }

  trackById(_: number, u: User): number {
    return u.id;
  }
}
