---
title: "Module 21 — gRPC"
subtitle: "Exercises"
module: "21-grpc"
lang: en-US
date: "2026-09-25"
---

# How to Work

1. The starter code is in `modules/21-grpc/exercise/`. Find the `TODO` comments (also in `src/main/proto/book_service.proto`).
2. The exercises need no Docker: the tests use the in-process transport.
3. Run the tests:

```bash
./mvnw -Pexercises -pl modules/21-grpc/exercise -am test
```

4. The exercise is done when all tests are green.
5. If you get stuck, read the hints first, then the solution in `modules/21-grpc/solution/`.

# Exercise 1 — A Backward-Compatible Schema Change (Medium)

**Goal:** the book gets a publication year, and a new RPC `SearchBooks` is declared. Clients that still use the old message (`old_book.proto`) must keep working.

**Tasks:**

- `TODO 1a` (`book_service.proto`) — add the field `year` (`int32`) to `Book`, without breaking old clients.
- `TODO 1b` (`exercise1.BookServiceImpl`) — implement `SearchBooks`: all books whose title contains `title_contains`, ignoring case, in one response.

**Hints:**

- Lesson section 4: a new field gets a new number. Which numbers are already used in `Book`?
- `Exercise1Test` reads the field through the message descriptor (`Book.getDescriptor().findFieldByName("year")`), so it compiles before the field exists.
- Without an override, the generated base class answers `UNIMPLEMENTED`.
- `SearchBooksResponse.newBuilder().addBooks(...)` collects the books.

**Acceptance criteria:** all 4 tests in `Exercise1Test` pass.

**Estimated time:** 25 minutes

# Exercise 2 — A Bulk Order as a Stream (Medium)

**Goal:** a client sends the lines of a large order as a stream (`PlaceOrders`) and receives one summary at the end.

**Tasks** (`exercise2.OrderLinesObserver`):

- `TODO 2a` — for each line: an unknown book or a quantity below 1 is rejected; otherwise the line is accepted and adds price × quantity to the total.
- `TODO 2b` — when the client has sent everything, answer with one `OrderSummary` and complete the call.

**Hints:**

- Lesson section 3.3 (client streaming): the state (counters, total) lives in the observer, one observer per call.
- `books.find(isbn)` returns an `Optional<Book>`.
- In the test: 2 × 8990 + 1 × 9500 = 27480 cents.

**Acceptance criteria:** `Exercise2Test` passes.

**Estimated time:** 25 minutes

# Exercise 3 — Don't Wait Forever (Easy)

**Goal:** a product page shows the title of a book. When the catalog is too slow, the page is shown without the title, instead of waiting.

**Tasks** (`exercise3.TitleLookup.titleWithin`):

- `TODO 3a` — call `GetBook` with a deadline of `timeout`.
- `TODO 3b` — `DEADLINE_EXCEEDED` gives `Optional.empty()`. Every other error is thrown on.

**Hints:**

- Lesson section 3.6: `stub.withDeadlineAfter(millis, TimeUnit.MILLISECONDS)`.
- `StatusRuntimeException.getStatus().getCode()`.
- The test sets the server's latency to 300 ms (`bookstore.latency`) and checks that the short call returns within 250 ms.

**Acceptance criteria:** both tests in `Exercise3Test` pass.

**Estimated time:** 15 minutes

# Extra Challenge (Optional)

Add a client interceptor (`@GlobalClientInterceptor`) that sends a header `x-request-id` with every call, and a server interceptor that reads it and writes it into the log. Test that the ID arrives on the server.
