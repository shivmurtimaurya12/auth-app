# Spring Boot Auth App — JWT Authentication & Authorization

A production-ready template for stateless JWT-based authentication and
role-based authorization built with Spring Boot 3.3 / Spring Security 6.

> **Spring Boot 4.x / Java 25 note:** Spring Boot 4.x and Java 25 are not yet
> GA (as of mid-2025). This project is written to be forward-compatible:
> no deprecated APIs are used, `jakarta.*` namespaces are used throughout, and
> the security configuration uses the lambda DSL introduced in Spring Security 6.
> When Spring Boot 4.x is released, update `<version>` in `pom.xml` and set
> `<java.version>25</java.version>`.

---

## Tech Stack

| Layer | Library |
|---|---|
| Framework | Spring Boot 3.3.x |
| Security | Spring Security 6 |
| JWT | JJWT 0.12.x |
| Persistence | Spring Data JPA + H2 |
| Validation | Jakarta Bean Validation |
| Boilerplate | Lombok |

---

## Features

- ✅ User registration with role assignment
- ✅ Login → returns **access token** (15 min) + **refresh token** (7 days)
- ✅ JWT validation via `OncePerRequestFilter`
- ✅ Token refresh endpoint
- ✅ Logout (invalidates refresh token)
- ✅ Role-based authorization: `ROLE_USER`, `ROLE_MODERATOR`, `ROLE_ADMIN`
- ✅ Method-level security via `@PreAuthorize`
- ✅ Global exception handler (validation, auth errors)
- ✅ Seeded demo users on startup

---

## Seeded Users

| Username | Password    | Roles              |
|----------|-------------|-------------------|
| admin    | Admin1234!  | ADMIN, USER       |
| mod      | Mod12345!   | MODERATOR, USER   |
| user     | User1234!   | USER              |

---

## API Endpoints

### Public

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login`    | Login → JWT |
| POST | `/api/auth/refresh`  | Refresh access token |

### Protected (Bearer token required)

| Method | Path | Roles |
|--------|------|-------|
| POST | `/api/auth/logout`        | Any authenticated |
| GET  | `/api/user/profile`       | USER, MOD, ADMIN |
| GET  | `/api/moderator/dashboard`| MOD, ADMIN |
| GET  | `/api/admin/dashboard`    | ADMIN |
| DELETE | `/api/admin/users/{id}` | ADMIN |

---

## Quick Start

```bash
./mvnw spring-boot:run
```

### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","email":"alice@example.com","password":"Alice123!"}'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"Alice123!"}'
```

### Access protected resource
```bash
curl http://localhost:8080/api/user/profile \
  -H "Authorization: Bearer <access_token>"
```

### Refresh token
```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<refresh_token>"}'
```

---

## H2 Console

Available at http://localhost:8080/h2-console  
JDBC URL: `jdbc:h2:mem:authdb`  Username: `sa`  Password: *(empty)*

---

## Migrating to PostgreSQL

Replace the H2 dependency and update `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/authdb
spring.datasource.username=postgres
spring.datasource.password=secret
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
```

---

## Production Checklist

- [ ] Replace H2 with PostgreSQL / MySQL
- [ ] Externalize `app.jwt.secret` to environment variable / secrets manager
- [ ] Enable HTTPS / TLS termination
- [ ] Add rate limiting (e.g. Bucket4j)
- [ ] Add email verification flow
- [ ] Add password reset flow
- [ ] Remove `DataInitializer` or gate it behind a profile
- [ ] Update Java version to 25 once Spring Boot 4.x GA is released
