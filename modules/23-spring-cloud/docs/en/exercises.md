---
title: "Module 23 — Spring Cloud"
subtitle: "Exercises"
module: "23-spring-cloud"
lang: en-US
date: "2026-09-25"
---

# How to Work

1. The starter code is in `modules/23-spring-cloud/exercise/`: a small gateway (Spring Cloud Gateway, WebFlux) in front of a catalog and a review service. Find the `TODO` comments (also in `application.yaml`).
2. The tests use WireMock for the two services, so they need no Docker:

```bash
./mvnw -Pexercises -pl modules/23-spring-cloud/exercise -am test
```

3. The exercise is done when all tests are green.
4. If you get stuck, read the hints first, then the solution in `modules/23-spring-cloud/solution/`.

# Exercise 1 — A New Route with an API Key (Medium)

**Goal:** the gateway also serves the review service, and only clients with a valid API key get through.

**Tasks:**

- `TODO 1a` (`application.yaml`) — a route `reviews`: `/api/reviews/**` to the service `review-service` through the load balancer.
- `TODO 1b` (`security.ApiKeyFilter`) — every request to `/api/**` needs a valid `X-Api-Key` header. Otherwise answer `401` and stop.

**Hints:**

- Lesson section 3.1: `uri: lb://…` and a `Path` predicate.
- In a `GlobalFilter`: `exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)` and `return exchange.getResponse().setComplete();` end the request without calling a service.
- The key comes from `bookstore.gateway.api-key` (`dev-key`).

**Acceptance criteria:** all 3 tests in `Exercise1Test` pass.

**Estimated time:** 30 minutes

# Exercise 2 — The Breaker Opens (Medium)

**Goal:** when the review service fails, the gateway answers with an empty, marked list and, after repeated failures, stops calling it for a while.

**Tasks:**

- `TODO 2a` (`application.yaml`) — a `CircuitBreaker` filter named `reviews` on the route of exercise 1, with the fallback `forward:/fallback/reviews`. `503` and `500` answers count as failures.
- `TODO 2b` (`fallback.FallbackController`) — `/fallback/reviews` answers `{"reviews": [], "fallback": true}`.

**Hints:**

- The filter arguments: `name`, `fallbackUri`, `statusCodes` (a list). Without `statusCodes`, only exceptions and timeouts count, and a `503` from the service goes to the client unchanged.
- The breaker settings (`resilience4j.circuitbreaker.instances.reviews`) are given: 4 calls, 50 %.
- `@RequestMapping("/fallback/reviews")` accepts every HTTP method.

**Acceptance criteria:** both tests in `Exercise2Test` pass.

**Estimated time:** 30 minutes

# Exercise 3 — Rotate the Key Without a Restart (Easy)

**Goal:** a leaked API key is replaced in the configuration. The gateway must use the new key after `POST /actuator/refresh`, without a restart.

**Tasks** (`security.ApiKeys`):

- `TODO 3` — make the bean read the configuration again after a refresh.

**Hints:**

- Lesson section 3.5: `@RefreshScope`.
- The test changes the environment and calls `ContextRefresher.refresh()`, which is what `/actuator/refresh` does.

**Acceptance criteria:** `Exercise3Test` passes.

**Estimated time:** 10 minutes

# Extra Challenge (Optional)

Put the exercise gateway in front of the compose system of the lesson: add it to `compose.yaml` on port 9100, with the review route pointing to a small WireMock container (`wiremock/wiremock`). Then stop the WireMock container and watch the breaker open (`/actuator/circuitbreakers`).
