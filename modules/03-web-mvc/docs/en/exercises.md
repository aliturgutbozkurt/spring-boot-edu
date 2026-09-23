---
title: "Module 03 — REST APIs with Web MVC"
subtitle: "Exercises"
module: "03-web-mvc"
lang: en-US
date: "2026-09-23"
---

# How to Work

1. The starter code is in `modules/03-web-mvc/exercise/`. Find the `TODO` comments (Java files and `application.yaml`).
2. Every exercise already has tests, and they stay **red** until you solve it. Some tests are green from the start. They are guard tests that check your solution does not break existing behaviour.
3. Run the tests:

```bash
./mvnw -Pexercises -pl modules/03-web-mvc/exercise -am test
```

4. The exercise is done when all tests are green.
5. If you get stuck, read the hints first, then the solution in `modules/03-web-mvc/solution/`.

> [!TIP]
> To run the tests of a single exercise: `-Dtest=Exercise1Test -Dsurefire.failIfNoSpecifiedTests=false`

# Exercise 1 — Author Endpoint (Easy)

**Goal:** write a small REST endpoint with the right status codes.

`Author` and an `AuthorRepository` with three sample authors are provided.

**Tasks** (package `exercise1`):

- `TODO 1a` — `GET /api/authors`: all authors.
- `TODO 1b` — `GET /api/authors/{id}`: one author. `404` if it does not exist.
- `TODO 1c` — `POST /api/authors`: take a validated `AuthorRequest` and return `201 Created`, a `Location` header and the new author.
- `TODO 1d` — `AuthorRequest`: the name must not be blank. The country must be exactly two upper-case letters (e.g. `TR`).

**Hints:**

- Lesson sections 3.1 and 3.2.
- The shortest way to a 404: `throw new ResponseStatusException(HttpStatus.NOT_FOUND, "...")`.
- For two upper-case letters: `@Pattern(regexp = "[A-Z]{2}")`.

**Acceptance criteria:** all 5 tests in `Exercise1Test` pass.

**Estimated time:** 30 minutes

# Exercise 2 — Order Errors as ProblemDetail (Medium)

**Goal:** turn domain exceptions into meaningful, machine-readable problem responses.

`OrderController` and `StockService` are provided. When the stock is too low, an `OutOfStockException` is thrown, and an unknown ISBN throws an `UnknownBookException`. Right now both end in a `500` error.

**Tasks** (`exercise2.OrderProblemHandler`):

- `TODO 2a` — `OutOfStockException` → `409 Conflict`. Type `https://springbootedu.com/problems/out-of-stock`, title `Out of stock`, detail = the exception message, `instance` = the request path. Extra fields: `isbn`, `requested`, `available`.
- `TODO 2b` — `UnknownBookException` → `404 Not Found`. Type `https://springbootedu.com/problems/book-not-found`, title `Book not found`, `instance` = the request path, extra field `isbn`.

**Hints:**

- Lesson section 3.3: `ProblemDetail.forStatusAndDetail(...)`, `setType`, `setTitle`, `setInstance`, `setProperty`.
- To get the request path, add an `HttpServletRequest` parameter to the method.

**Acceptance criteria:** all 3 tests in `Exercise2Test` pass.

**Estimated time:** 30 minutes

# Exercise 3 — Two Versions of the Order Summary (Hard)

**Goal:** serve two incompatible response formats on the same URL with Spring Framework 7 API versioning.

Today, `GET /api/order-summaries/{id}` returns a flat v1 response. The mobile app wants the currency, a readable status label and the order lines. Existing clients must not break.

**Tasks:**

- `TODO 3a` — `application.yaml`: enable versioning with the `API-Version` header.
- `TODO 3b` — supported versions `1` and `2`; requests without the header get `1`.
- `TODO 3c` — mark the existing method as version `1`.
- `TODO 3d` — write the version `2` method for the same URL, and the `OrderSummaryV2` record:

```json
{"id": 1001,
 "total": {"amount": 145.00, "currency": "TRY"},
 "status": {"code": "PAID", "label": "Ödendi / Paid"},
 "lines": [{"title": "Effective Java", "quantity": 1}, {"title": "Java Puzzlers", "quantity": 1}]}
```

**Hints:**

- Lesson section 3.5: the `spring.mvc.apiversion.*` settings and `@GetMapping(path = "...", version = "2")`.
- Nested records become nested objects in JSON: `record Money(BigDecimal amount, String currency)`.
- The status label comes from `OrderSummary.Status.label()`.

**Acceptance criteria:** all 4 tests in `Exercise3Test` pass.

**Estimated time:** 45 minutes

# Bonus Challenge (Optional)

Document the author endpoint with springdoc (`@Tag`, `@Operation`). Write a test that uses `@SpringBootTest(webEnvironment = RANDOM_PORT)` and `RestTestClient` instead of `@WebMvcTest`, and verify the `POST /api/authors` flow against a real server.
