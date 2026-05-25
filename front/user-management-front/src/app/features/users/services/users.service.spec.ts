import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { UsersService } from './users.service';
import { environment } from '../../../../environments/environment';

describe('UsersService', () => {
  let service: UsersService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(UsersService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should list users with query params', async () => {
    const promise = service.list({ search: 'omar', page: 2, size: 5, sortBy: 'email', direction: 'DESC' });

    const req = httpMock.expectOne(r => r.url === `${environment.apiBaseUrl}${environment.apiPrefix}/users`);
    expect(req.request.params.get('search')).toBe('omar');
    expect(req.request.params.get('page')).toBe('2');
    expect(req.request.params.get('size')).toBe('5');
    expect(req.request.params.get('sortBy')).toBe('email');
    expect(req.request.params.get('direction')).toBe('DESC');

    req.flush({ content: [], page: 2, size: 5, totalElements: 0, totalPages: 0, first: false, last: true });

    const res = await promise;
    expect(res.page).toBe(2);
    expect(res.content.length).toBe(0);
  });

  it('should get user by id', async () => {
    const p = service.get(1);
    const req = httpMock.expectOne(`${environment.apiBaseUrl}${environment.apiPrefix}/users/1`);
    req.flush({ id: 1, firstName: 'O', lastName: 'G', email: 'a@b.com', phone: null, active: true, deleted: false, version: 0, createdAt: '', updatedAt: '', createdBy: 'x', updatedBy: 'y' });
    const user = await p;
    expect(user.id).toBe(1);
  });

  it('should create user', async () => {
    const p = service.create({ firstName: 'O', lastName: 'G', email: 'a@b.com', phone: null, active: true });
    const req = httpMock.expectOne(`${environment.apiBaseUrl}${environment.apiPrefix}/users`);
    expect(req.request.method).toBe('POST');
    req.flush({ id: 10, firstName: 'O', lastName: 'G', email: 'a@b.com', phone: null, active: true, deleted: false, version: 0, createdAt: '', updatedAt: '', createdBy: 'x', updatedBy: 'y' });
    const user = await p;
    expect(user.id).toBe(10);
  });

  it('should update user', async () => {
    const p = service.update(2, { firstName: 'N', lastName: 'N', email: 'n@n.com', phone: '1', active: false });
    const req = httpMock.expectOne(`${environment.apiBaseUrl}${environment.apiPrefix}/users/2`);
    expect(req.request.method).toBe('PUT');
    req.flush({ id: 2, firstName: 'N', lastName: 'N', email: 'n@n.com', phone: '1', active: false, deleted: false, version: 0, createdAt: '', updatedAt: '', createdBy: 'x', updatedBy: 'y' });
    const user = await p;
    expect(user.active).toBeFalse();
  });

  it('should delete user', async () => {
    const p = service.delete(3);
    const req = httpMock.expectOne(`${environment.apiBaseUrl}${environment.apiPrefix}/users/3`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
    await expectAsync(p).toBeResolved();
  });
});
