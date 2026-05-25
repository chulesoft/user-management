import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../environments/environment';

function toBasicAuth(username: string, password: string): string {
  return btoa(`${username}:${password}`);
}

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const { username, password } = environment.basicAuth;
  const authHeader = `Basic ${toBasicAuth(username, password)}`;

  return next(req.clone({
    setHeaders: {
      Authorization: authHeader,
      'X-Correlation-Id': crypto.randomUUID?.() ?? `${Date.now()}`
    }
  }));
};

export { toBasicAuth };
