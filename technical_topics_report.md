# RidePlatform Technical Topics Breakdown

This document provides a comprehensive, folder-by-folder and file-by-file breakdown of all the technical concepts, design patterns, and technologies implemented in the CommuterService (RidePlatform) codebase.

---

## 📂 Root Directory (Infrastructure & Configuration)

### 📄 `pom.xml`
*   **Dependency Management:** Maven project structure and dependency resolution.
*   **Spring Boot Starter Ecosystem:** Auto-configuration and dependency grouping (`spring-boot-starter-web`, `data-jpa`, `security`, `validation`).
*   **Database Integration:** PostgreSQL JDBC driver.
*   **Database Migrations:** Flyway integration for version-controlled schema evolution.
*   **API Documentation:** OpenAPI 3.0 / Swagger integration via `springdoc-openapi`.
*   **Security & Auth:** JJWT (Java JWT) for token processing.
*   **Rate Limiting:** Bucket4j core library integration.

### 📄 `docker-compose.yml`
*   **Containerization:** Docker Compose orchestration.
*   **Database Hosting:** Running PostgreSQL 15 on Alpine Linux.
*   **Data Persistence:** Docker volumes (`postgres_data`) for persistent database storage across container restarts.
*   **Network & Ports:** Port mapping and environment variable injection for containers.

### 📄 `.env` & `config.properties.example`
*   **Externalized Configuration:** 12-Factor App methodology for storing secrets and environment-specific variables outside of source code.

### 📄 `.gitignore`
*   **Source Control Hygiene:** Excluding build artifacts, IDE configurations, OS-specific files, and sensitive environment variables from Git.

---

## 📂 `src/main/resources/` (App Configuration & DB Scripts)

### 📄 `application.yml`
*   **Spring Profiles & Configuration:** Application naming, datasource setup.
*   **Environment Variable Resolution:** Using Spring Expression Language (SpEL) with fallbacks (e.g., `${DB_URL:jdbc...}`).
*   **Connection Pooling:** HikariCP tuning (`maximum-pool-size`, `connection-timeout`) for performance and DoS resilience.
*   **JPA/Hibernate Config:** Disabling `ddl-auto` in favor of Flyway, configuring PostgreSQL dialect, and disabling SQL logging for production.

### 📂 `db/migration/`
#### 📄 `V1__Initial_Schema.sql`
*   **DDL (Data Definition Language):** Table creation and schema design.
*   **UUIDs:** Use of `gen_random_uuid()` via `pgcrypto` extension for non-enumerable, distributed primary keys.
*   **Data Integrity (Constraints):** Extensive use of `UNIQUE`, `NOT NULL`, `CHECK` (e.g., `rating BETWEEN 1 AND 5`), and `FOREIGN KEY` (with `ON DELETE CASCADE`) constraints.
*   **Database Triggers & Functions:** PL/pgSQL function (`rp_update_timestamp`) and triggers to automatically manage `updated_at` timestamps at the database level.
*   **Database Indexing:** Creating indices (`CREATE INDEX`) on frequently queried columns to optimize read performance.

---

## 📂 `src/main/java/com/elite/rideplatform/` (Source Code)

### 📂 `common/` (Shared Base Classes)
*   **📄 `BaseEntity.java`:** JPA `@MappedSuperclass` for inheritance. Implements automated UUID generation (`@GeneratedValue(strategy = GenerationType.UUID)`) and audit fields (`created_at`, `updated_at`).
*   **📄 `User.java`:** Abstract class extending `BaseEntity` to share common fields (name, email, phone) between `Passenger` and `Partner` (DRY principle).
*   **📄 `GlobalExceptionHandler.java`:** Global Error Handling using `@ControllerAdvice`. Implements typed error responses, suppresses internal stack traces (Security), and handles specific exceptions like `ObjectOptimisticLockingFailureException` (returning HTTP 409 Conflict).

### 📂 `exception/`
*   **📄 `DomainException.java` & others:** Custom Exception Hierarchy. Encapsulating domain-specific errors (e.g., `InsufficientWalletBalanceException`) as Runtime exceptions.

### 📂 `admin/`
*   **📄 `AdminController.java`:** REST Controller for admin operations. Demonstrates potential for Role-Based Access Control via `@PreAuthorize`.
*   **📄 `AdminReportingRepository.java`:** **Hybrid Data Access Pattern**. Uses `JdbcTemplate` instead of JPA for complex analytical queries (aggregations, `GROUP BY`, `JOIN`s) to optimize performance and bypass ORM overhead for reporting.

### 📂 `partner/` & `passenger/`
*   **📄 `*Service.java`:** Business logic layer. Implements `@Transactional` boundaries, BCrypt password hashing (`PasswordEncoder`), and input validation.
*   **📄 `*Repository.java`:** Spring Data JPA interface (`JpaRepository`) utilizing auto-generated queries (`existsByEmailOrPhoneNo`).
*   **📄 `*Controller.java`:** REST API endpoints (`@RestController`, `@PostMapping`). Uses Data Transfer Objects (DTOs) like `PassengerRegistrationRequest` to decouple API contracts from internal entity structures.

### 📂 `pricing/`
*   **📄 `PricingService.java`:** Financial computation using `BigDecimal` to prevent floating-point inaccuracies. Implements the **Haversine formula** for geographic distance calculation.
*   **📄 `SurgePricingJob.java`:** Background task scheduling using Spring's `@Scheduled` annotation to run periodic jobs (cron-like behavior).
*   **📄 `SurgePricingRepository.java`:** Custom JPQL (Java Persistence Query Language) to perform geographic bounding-box queries for surge zones.
*   **📄 Domain Entities (`FareRule`, `Offer`, `SurgePricing`):** Modeling complex business rules (percentage vs. flat discounts, usage limits, active windows).

### 📂 `security/`
*   **📄 `SecurityConfig.java`:** Spring Security configuration (`SecurityFilterChain`). Implements **Stateless Sessions** (`SessionCreationPolicy.STATELESS`), registers custom filters, configures `BCryptPasswordEncoder`, and intentionally disables CSRF protection (appropriate for stateless JWT APIs).
*   **📄 `JwtUtil.java`:** Cryptography. Generating and validating JSON Web Tokens (JWT) using HMAC-SHA256 signatures (`SignatureAlgorithm.HS256`).
*   **📄 `JwtAuthenticationFilter.java`:** Custom HTTP Filter (`OncePerRequestFilter`). Intercepts requests, extracts the `Bearer` token, validates it, and populates the `SecurityContextHolder`.
*   **📄 `CustomUserDetailsService.java`:** Multi-entity authentication. Queries both `Passenger` and `Partner` tables and assigns Spring Security `GrantedAuthority` roles (`ROLE_PASSENGER`, `ROLE_PARTNER`) for Role-Based Access Control (RBAC).
*   **📄 `RateLimitingFilter.java`:** API Protection. Implements the **Token Bucket algorithm** via Bucket4j to provide per-IP rate limiting (100 req/min), utilizing `ConcurrentHashMap` for thread-safe caching.
*   **📄 `AuthController.java`:** Explicit endpoint for authentication that verifies roles and issues JWTs.

### 📂 `trip/`
*   **📄 `Trip.java`:** Complex JPA Entity modeling relationships (`@ManyToOne`). Uses **Lazy Loading** (`FetchType.LAZY`) to prevent N+1 query performance issues.
*   **📄 `TripStatus.java`:** Enumeration defining the valid states of a trip.
*   **📄 `TripService.java`:** **Finite State Machine (FSM)** implementation. Strictly validates state transitions (e.g., REQUESTED -> ACCEPTED -> ARRIVED) and enforces authorization (verifying the correct partner is modifying the trip). Also orchestrates cross-domain logic (calling `WalletService` upon completion).
*   **📄 `TripStatusLog.java`:** Audit logging entity to maintain a historical ledger of all state changes.

### 📂 `wallet/`
*   **📄 `Wallet.java`:** Concurrency Control. Implements **Optimistic Locking** using the JPA `@Version` annotation to prevent race conditions during concurrent balance updates.
*   **📄 `WalletService.java`:** Financial transactions. Enforces strict `@Transactional` boundaries and pre-condition checks (guarding against insufficient funds).
*   **📄 `WalletTransaction.java`:** Implements an **Immutable Double-Entry Ledger Pattern**. Records every financial movement (CREDIT/DEBIT) alongside the resulting balance for traceability.
