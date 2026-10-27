# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

Infrastructure (Postgres on 5432, RabbitMQ on 5672 + mgmt UI on 15672) must be running before the app:

```
docker compose up -d
```

Build / run / test:

```
./gradlew build                                  # compile + run tests
./gradlew bootRun                                # run the app on :8081
./gradlew test                                   # all tests
./gradlew test --tests "com.booking.booking.BookingApplicationTests"   # single test class
./gradlew test --tests "*.someMethod"            # single test method
```

The app expects `JWT_SECRET` (≥32 bytes) and optionally `ADMIN_PASSWORD` as env vars; both have dev defaults in `application.yml`. Admin user (`admin@booking.local`) is seeded on startup if missing (see `AdminSeeder`).

## Architecture

Kotlin + Spring Boot 4.1.2 on JVM 21, Gradle Kotlin DSL. Package root: `com.booking.booking`.

**Persistence contract.** `spring.jpa.hibernate.ddl-auto: validate` — Hibernate will refuse to start if entity mappings diverge from the schema. All schema changes go through Flyway migrations in `src/main/resources/db/migration/` (versioned `V<n>__<name>.sql`); never rely on JPA to create or alter tables.

**Kotlin ↔ Spring interop.** Two build-level choices matter when editing entities or validated DTOs:
- `allOpen` plugin opens classes annotated `@Entity`, `@MappedSuperclass`, `@Embeddable` so JPA can proxy them — do not mark these `open` manually.
- `-Xannotation-default-target=param-property` (in `build.gradle.kts`) makes annotations on constructor params apply to both the parameter and the backing property. Validation annotations on DTO fields are written as `@field:NotBlank`, `@field:Email`, etc. — keep the `@field:` prefix for Jakarta Validation to pick them up.

**Auth / security flow.** Stateless JWT via Spring Security's OAuth2 Resource Server (HS256, symmetric secret from `app.jwt.secret`). Flow:
1. `AuthController` exposes `POST /auth/register`, `POST /auth/login`, `GET /me`, `GET /admin/ping`.
2. `AuthService` hashes with `BCryptPasswordEncoder` and delegates token issuance to `TokenService`, which puts `sub=userId`, `email`, and `roles=["USER"|"ADMIN"]` into the JWT.
3. `SecurityConfig.filterChain` is the single source of truth for URL authorization — rules are matched top-to-bottom. `/actuator/health/**` and `/actuator/info` are public; the rest of `/actuator/**` and all of `/admin/**` require `ROLE_ADMIN`; `/auth/register` and `/auth/login` are public; everything else requires authentication.
4. The JWT `roles` claim is mapped to Spring authorities via `JwtGrantedAuthoritiesConverter` with prefix `ROLE_` — so `roles: ["ADMIN"]` in the token becomes authority `ROLE_ADMIN`, which is what `hasRole("ADMIN")` checks. If you add a new role, update both the `Role` enum and the DB `CHECK` constraint in the migrations.

**Config properties.** `JwtProperties` is `@ConfigurationProperties("app.jwt")`; `@ConfigurationPropertiesScan` on `BookingApplication` picks it up automatically — new `@ConfigurationProperties` classes do not need manual registration.

**Server port is 8081**, not the Spring default 8080.
