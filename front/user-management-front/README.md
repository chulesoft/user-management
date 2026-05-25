# User Management UI (Angular 20)

Frontend for the User Management CRUD backend.

## Features
- Angular 20 standalone components
- Promise-based async service layer
- Search bar + pagination
- Lazy-loaded routes + `@defer` rendering for list table
- Basic Auth interceptor (defaults in `src/environments/environment.ts`)
- TDD-first: unit tests for services/components with Karma coverage thresholds at 100%

## Run
```bash
npm install
npm start
```

## Test (100% coverage enforced)
```bash
npm test
```

## Backend
Expected backend base URL: `http://localhost:8080/api/v1`

Endpoints used:
- GET `/users`
- GET `/users/{id}`
- POST `/users`
- PUT `/users/{id}`
- DELETE `/users/{id}`
