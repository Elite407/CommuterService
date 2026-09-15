# RideVault-Backend — Scaling Roadmap

**Base repo:** RideVault-Backend
**Goal:** Turn a console-based JDBC/PostgreSQL app into a real deployed backend with a thin frontend
**Explicitly out of scope:** LangChain / GenAI features (set aside for this project)

---

## 1. Current State — What Exists Today

**What's already solid and worth keeping:**
- Layered architecture: Model → Service → DAO is followed consistently, not just in name.
- Every query uses `PreparedStatement` — no SQL injection.
- `WalletDAO` does real transactions (`setAutoCommit(false)`, commit/rollback) for money movement.
- Ride acceptance uses a conditional `UPDATE ... WHERE status = 'PENDING'` — a legitimate optimistic-concurrency pattern that actually prevents double-booking.
- The DDL is well normalized: proper foreign keys, `CHECK` constraints, indexes on frequently filtered columns.
- README is thorough and honest about current limitations.

**What's currently broken or missing:**
- No HTTP layer at all — `Main.java` is a `Scanner`-based console menu (Maven artifact is literally named `ridevault-cli`).
- Passwords are stored and compared in plaintext, despite the column being named `password_hash`.
- DB credentials are hardcoded in `DatabaseConnection.java` and committed to git.
- No connection pooling — a brand-new JDBC connection is opened on every single call.
- Zero tests anywhere in the repo.
- Single squashed commit — no real development history.
- No `.gitignore`.
- Wallet withdrawal has a check-then-act race condition (reads balance, checks in Java, then issues a separate `UPDATE`) — not safe under concurrent withdrawals.
- Several schema tables are defined but never used by any DAO/service: `Vehicle`, `Payment`, `Rating`, `Offers`, `Fare_Rules`, `Surge_Pricing`, `Ride_Status_Log`.

---

## 2. Phase 0 — Fix First (Security & Hygiene)

These aren't polish, they're broken:

- [ ] Hash passwords with BCrypt via Spring Security's `PasswordEncoder`.
- [ ] Move DB credentials to `application.yml` / environment variables.
- [ ] Add a proper `.gitignore` (target/, .env, IDE files).
- [ ] Add request validation (`@Valid` + Bean Validation) once real endpoints exist.

---

## 3. Phase 1 — Core Migration: CLI → REST API

- [ ] Introduce Spring Boot + Spring Web.
- [ ] Replace the `Main.java` menu loop with `@RestController` classes.
- [ ] Keep the existing Service layer — it maps onto Spring `@Service` beans with minimal change.
- [ ] Decide: JDBC Template (keeps current control, less new surface area) vs. full JPA/Hibernate (closer to a typical Spring Boot resume story, and gives you a real N+1 query problem to find and fix — JDBC alone won't produce that failure mode).
- [ ] Separate DTOs from entities/models.
- [ ] Global exception handling via `@ControllerAdvice`.
- [ ] API docs via springdoc-openapi/Swagger.

---

## 4. Phase 2 — Auth & Security Layer

- [ ] JWT stateless authentication (Spring Security).
- [ ] Role-based access control — Rider vs. Driver vs. Admin gating different endpoints.
- [ ] Rate limiting (Bucket4j) on login and ride-request endpoints.
- [ ] Google OAuth2 — optional, nice-to-have, not essential to the core story.

---

## 5. Phase 3 — Data Layer Improvements

- [ ] Add HikariCP connection pooling (currently: no pool at all — real, benchmarkable before/after).
- [ ] Wire up the currently unused schema tables as real features: `Vehicle`, `Payment`, `Rating`, `Offers`, `Fare_Rules`, `Surge_Pricing`, `Ride_Status_Log`.
- [ ] Add Flyway for versioned schema migrations instead of a static `DDL.sql`.

---

## 6. Phase 4 — Feature Expansion

This is where endpoint count actually grows:

- [ ] Fare calculation engine using `Fare_Rules` + `Surge_Pricing` — real dynamic pricing, not a flat fare.
- [ ] Ratings & reviews after ride completion.
- [ ] Ride history with pagination and filters.
- [ ] Offers/promo redemption.
- [ ] Admin endpoints — manage drivers/riders, basic stats.
- [ ] *(Stretch, optional)* Real-time driver location via WebSocket.

---

## 7. Phase 5 — Testing

The strongest, most honest part of the story if done properly:

- [ ] Unit tests (JUnit 5 + Mockito) for service logic — especially fare calculation and wallet rules.
- [ ] Integration tests (Spring Boot Test + Testcontainers) against a real Postgres instance, not mocks.
- [ ] Write a concurrency test that fires parallel withdrawal requests against the wallet to reproduce the race condition, then fix it with `SELECT FOR UPDATE` or an atomic conditional update. This "found and fixed a concurrency bug via testing" is a stronger, more truthful story than a fabricated N+1 fix.
- [ ] Load test with k6 once the API is live — get real p95 latency and req/s numbers.
- [ ] If using JPA/Hibernate: enable SQL logging (or p6spy) to catch and document a real N+1 query fix.

---

## 8. Phase 6 — Deployment

- [ ] Docker Compose: app + Postgres + nginx.
- [ ] nginx as reverse proxy with HTTPS.
- [ ] Deploy to a free/cheap tier (Render, Railway, Azure free tier) so the resume link resolves to something live.
- [ ] GitHub Actions running tests on push.

---

## 9. Phase 7 — Frontend (Slight, As Scoped)

- [ ] One or two screens covering the core loop: book a ride, view wallet/ride history, driver accepts a ride.
- [ ] Skip a full multi-page UI — a working demo of the main flow is enough to back a "full-stack" claim.

---

## 10. Attribution & Polish

- [ ] Fork the actual repo on GitHub rather than copying files into a fresh repo.
- [ ] Keep the MIT license notice.
- [ ] Note in the README what was built on top of the original base and what was added/changed — reads better in an interview than an unattributed rewrite, and makes the scope of your own work explicit.

---

## 11. Realistic Minimum Scope

Doing everything above is a multi-week project. A strong, fully honest, achievable version is:

1. Password hashing + hardcoded-credential fix
2. Spring Boot REST layer + JWT auth
3. HikariCP with a benchmarked before/after improvement
4. The wallet concurrency bug — found and fixed via a concurrency test
5. k6 load test numbers from a real deployed instance
6. Docker Compose deployment with a live URL

Everything else (OAuth2, WebSockets, full feature buildout to 44 endpoints, CI) is additive on top of that core, not required to have a legitimate, defensible resume line.
