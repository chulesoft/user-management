import { TestBed } from '@angular/core/testing';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { authInterceptor, toBasicAuth } from './auth.interceptor';
import { environment } from '../../environments/environment';

describe('authInterceptor', () => {
  it('should add Authorization header using basic auth', () => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting()
      ]
    });

    const http = TestBed.inject(HttpClient);
    const ctrl = TestBed.inject(HttpTestingController);

    http.get('/test').subscribe();

    const req = ctrl.expectOne('/test');
    const expected = `Basic ${toBasicAuth(environment.basicAuth.username, environment.basicAuth.password)}`;
    expect(req.request.headers.get('Authorization')).toBe(expected);
    expect(req.request.headers.has('X-Correlation-Id')).toBeTrue();
    req.flush({ ok: true });
    ctrl.verify();
  });
});
