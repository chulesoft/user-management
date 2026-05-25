import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

import { environment } from '../../../../environments/environment';
import { PageResponse, User, UserRequest } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class UsersService {
  private readonly baseUrl = `${environment.apiBaseUrl}${environment.apiPrefix}/users`;

  constructor(private http: HttpClient) {}

  list(params: { search?: string; page?: number; size?: number; sortBy?: string; direction?: 'ASC'|'DESC' } = {}): Promise<PageResponse<User>> {
    let httpParams = new HttpParams()
      .set('page', String(params.page ?? 0))
      .set('size', String(params.size ?? 10))
      .set('sortBy', params.sortBy ?? 'id')
      .set('direction', params.direction ?? 'ASC');

    if (params.search && params.search.trim().length > 0) {
      httpParams = httpParams.set('search', params.search.trim());
    }

    return firstValueFrom(this.http.get<PageResponse<User>>(this.baseUrl, { params: httpParams }));
  }

  get(id: number): Promise<User> {
    return firstValueFrom(this.http.get<User>(`${this.baseUrl}/${id}`));
  }

  create(request: UserRequest): Promise<User> {
    return firstValueFrom(this.http.post<User>(this.baseUrl, request));
  }

  update(id: number, request: UserRequest): Promise<User> {
    return firstValueFrom(this.http.put<User>(`${this.baseUrl}/${id}`, request));
  }

  delete(id: number): Promise<void> {
    return firstValueFrom(this.http.delete<void>(`${this.baseUrl}/${id}`));
  }
}
