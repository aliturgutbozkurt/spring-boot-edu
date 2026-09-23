---
title: "Module 01 — Spring Core Container: IoC and Dependency Injection"
subtitle: "Exercises"
module: "01-core-container"
lang: en-US
date: "2026-09-23"
---

# How to Work

1. The starter code is in `modules/01-core-container/exercise/`. Find the `TODO` comments.
2. Every exercise already has tests, and they stay **red** until you solve it.
3. Run the tests:

```bash
./mvnw -Pexercises -pl modules/01-core-container/exercise test
```

4. The exercise is done when all tests are green.
5. If you get stuck, read the hints first, then the solution in `modules/01-core-container/solution/`.

> [!TIP]
> To run the tests of a single exercise: `-Dtest=Exercise1Test`

# Exercise 1 — Shipping Strategies (Easy)

**Goal:** inject several implementations of one interface as a `Map` and build a strategy pattern.

The bookstore offers three shipping methods. Each method is a `ShippingCalculator` bean and **the bean name is the method name** (`standard`, `express`). Spring passes all of these beans to a constructor parameter of type `Map<String, ShippingCalculator>`, keyed by bean name.

**Tasks** (package `exercise1`):

- `TODO 1a` — make `StorePickupShipping` a bean named `pickup` and return a cost of `0.00`.
- `TODO 1b` — `ShippingService.availableMethods()`: return the method names in alphabetical order.
- `TODO 1c` — `ShippingService.cost(...)`: find the calculator for the method. For an unknown method throw an `IllegalArgumentException` whose message contains the method name and the list of valid methods.

**Hints:**

- Look at `StandardShipping`: the bean name is given with `@Component("...")`.
- Remember the `List<NotificationChannel>` injection in lesson section 3.6. A `Map` works the same way.

**Acceptance criteria:** all 4 tests in `Exercise1Test` pass.

**Estimated time:** 20 minutes

# Exercise 2 — Gift Wrap Feature Flag (Medium)

**Goal:** switch a feature on and off through configuration, and make sure there is **exactly one** bean in every case.

If `bookstore.gift-wrap.enabled=true`, paid gift wrapping (`PaidGiftWrap`) is offered. If the value is `false` **or not set at all**, `NoGiftWrap` is used. The price can be changed with `bookstore.gift-wrap.price` and defaults to `15.00`.

**Tasks** (`exercise2.GiftWrapConfiguration`):

- `TODO 2a` — add a `@Bean` method that returns a `PaidGiftWrap` when the flag is `true`.
- `TODO 2b` — take the price from the property, and use `15.00` when it is missing.
- `TODO 2c` — add a `@Bean` method that returns a `NoGiftWrap` when the flag is `false` **or missing**.

**Hints:**

- Lesson section 3.5: `@ConditionalOnProperty(name = ..., havingValue = ..., matchIfMissing = ...)`.
- A property with a default: `@Value("${key:default}")` also works on a parameter of a `@Bean` method.

**Acceptance criteria:** all 4 tests in `Exercise2Test` pass.

**Estimated time:** 30 minutes

# Exercise 3 — Followed Author Notification (Hard)

**Goal:** publish an event, handle it with a conditional listener, and control the order of listeners.

When a new book is added, `BookPublisher` must publish a `BookAddedEvent`. If the customer follows the book's author, `FollowerNotifier` must write a notification to the `Inbox`. The provided `AuditTrail` listener records every book with `@Order(10)`. The notification must be written **before** the audit entry.

**Tasks** (package `exercise3`):

- `TODO 3a` — receive an `ApplicationEventPublisher` through the `BookPublisher` constructor.
- `TODO 3b` — publish a `BookAddedEvent` in `publish(...)`.
- `TODO 3c` — make `FollowerNotifier.onBookAdded` an event listener.
- `TODO 3d` — the listener must run only if the author is followed.
- `TODO 3e` — the listener must run before `AuditTrail`.
- `TODO 3f` — write exactly this message to the inbox: `Takip ettiğiniz yazar / Followed author: <author> — <title>`

**Hints:**

- Lesson section 3.7: `@EventListener` and `@Order`.
- You can write the condition as an `if` inside the method. The more elegant solution refers to a bean from SpEL: `condition = "@followedAuthors.isFollowed(#event.author())"`.

**Acceptance criteria:** all 3 tests in `Exercise3Test` pass.

**Estimated time:** 45 minutes

# Bonus Challenge (Optional)

Register the shipping methods of Exercise 1 with a `BeanRegistrar`, reading them from a property such as `bookstore.shipping.methods=standard,express` (lesson section 3.6). Methods missing from the configuration must not become beans. Write your own test as well.
