# User Management CRUD System

Professional Spring Boot backend for managing users using **TDD**, layered architecture, async WebFlux endpoints, JPA persistence for MySQL, Docker Compose, Swagger/OpenAPI, Flyway migrations, Basic Auth security, and JaCoCo coverage enforcement.

> Note: The requirement `loom book` was interpreted as **Lombok**. Java 17 is used as requested. Java virtual threads from Project Loom require Java 21+, so they are not enabled in this Java 17 project.

## Key Improvements Implemented

- Pagination validation (`page >= 0`, `1 <= size <= 100`)
- Sorting whitelist (prevents invalid sort fields)
- Search filter (`search` param) across firstName/lastName/email
- Stable paginated API response (`PageResponse<T>`) instead of Spring `Page`
- Soft delete (`deleted=true`, excluded from list/find)
- Optimistic locking (`@Version`)
- Flyway migrations (schema managed via `db/migration`)
- Security (HTTP Basic Auth for `/api/v1/**`)
- Environment-based CORS (`app.cors.allowed-origins`)
- Correlation ID header (`X-Correlation-Id`)
- Structured logging style (event=... key=value)
- Testcontainers MySQL test for Flyway

## Security

All `/api/v1/**` endpoints require HTTP Basic Auth.

Default credentials:

- Username: `admin`
- Password: `password`

Override via env vars:

- `APP_SECURITY_USERNAME`
- `APP_SECURITY_PASSWORD`

Swagger is public:

- `http://localhost:8080/swagger-ui.html`

## API Endpoints

Base URL: `http://localhost:8080/api/v1/users`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/users?search=omar&page=0&size=10&sortBy=id&direction=ASC` | Paginated list + optional search |
| GET | `/api/v1/users/{id}` | Get user by id |
| POST | `/api/v1/users` | Create |
| PUT | `/api/v1/users/{id}` | Update |
| DELETE | `/api/v1/users/{id}` | Soft delete |

### Request Body

```json
{
  "firstName": "Omar",
  "lastName": "Garcia",
  "email": "omar@example.com",
  "phone": "8112345678",
  "active": true
}
```

### Page Response

```json
{
  "content": [],
  "page": 0,
  "size": 10,
  "totalElements": 0,
  "totalPages": 0,
  "first": true,
  "last": true
}
```

## Run with Maven

```bash
mvn clean verify
mvn spring-boot:run
```

Run with dev seed data:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Run with Docker Compose

```bash
docker compose up --build
```

## Test

```bash
mvn clean test
mvn clean verify
```

Coverage report:

`target/site/jacoco/index.html`
