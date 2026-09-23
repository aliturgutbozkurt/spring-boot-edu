---
title: "Module 02 — Configuration and Auto-Configuration"
subtitle: "Exercises"
module: "02-configuration"
lang: en-US
date: "2026-09-23"
---

# How to Work

1. The starter code is in `modules/02-configuration/exercise/`. Find the `TODO` comments (Java files, `application.yaml` and `META-INF/spring/...imports`).
2. Every exercise already has tests, and they stay **red** until you solve it.
3. Run the tests:

```bash
./mvnw -Pexercises -pl modules/02-configuration/exercise -am test
```

4. The exercise is done when all tests are green.
5. If you get stuck, read the hints first, then the solution in `modules/02-configuration/solution/`.

> [!TIP]
> To run the tests of a single exercise: `-Dtest=Exercise1Test -Dsurefire.failIfNoSpecifiedTests=false`

# Exercise 1 — Validated Opening Hours (Easy)

**Goal:** write a typed and validated `@ConfigurationProperties` record.

The bookstore's opening hours are defined under `bookstore.opening-hours.*`. An invalid setting (e.g. closing before opening) must stop the application from starting.

**Tasks** (`exercise1.OpeningHoursProperties`):

- `TODO 1a` — mark the record so that it is validated at startup.
- `TODO 1b` — make `opens` and `closes` required.
- `TODO 1c` — `maxReservations` must be between 1 and 10, and 3 when not set.
- `TODO 1d` — `closedDays` must be an empty list when not set.
- `TODO 1e` — validation must fail when the closing time is not after the opening time.
- `TODO 1f` — `isOpen(day, time)`: `true` on days that are not closed, from opening (inclusive) to closing (exclusive).

**Hints:**

- Lesson section 3.2: `@Validated`, `@NotNull`, `@Min`/`@Max`, `@DefaultValue`.
- For a cross-field rule, put `@AssertTrue(message = "...")` on a `boolean` method named `isXxx()`. Values may be `null`, so check that first.

**Acceptance criteria:** all 5 tests in `Exercise1Test` pass.

**Estimated time:** 30 minutes

# Exercise 2 — Pricing per Profile (Medium)

**Goal:** produce different prices with the same code, using profile files and a profile group.

`PricingProperties` (`bookstore.pricing.*`) is provided. You will write the values into the YAML files.

**Tasks** (`exercise/src/main/resources/`):

- `TODO 2a` — `application.yaml`: default shipping fee `29.90`, discount `0`.
- `TODO 2b` — `application-campaign.yaml`: shipping `0.00`, discount `20`. `application-express.yaml`: shipping `49.90`.
- `TODO 2c` — a profile group named `black-friday` that activates `campaign` first, then `express`.

**Hints:**

- Lesson section 3.4: `spring.profiles.group.<name>: profile1, profile2`.
- Within a group, the profile that comes **later** wins for the same key. That is why on black-friday the discount comes from campaign and the shipping fee from express.
- Write money values in quotes: `"29.90"`.

**Acceptance criteria:** all 4 tests in `Exercise2Test` pass.

**Estimated time:** 30 minutes

# Exercise 3 — Your Own Auto-Configuration (Hard)

**Goal:** write an auto-configuration that is guarded by conditions, backs off when the user has a bean, and is found through the imports file.

`ExchangeRateService`, `FixedExchangeRateService` and `ExchangeRateProperties` (`bookstore.exchange.*`) are provided. What is missing is the auto-configuration that brings them together.

**Tasks** (`exercise3.ExchangeRateAutoConfiguration` and the imports file):

- `TODO 3a` — make the class an auto-configuration.
- `TODO 3b` — it must apply only when `bookstore.exchange.enabled` is `true` or not set at all.
- `TODO 3c` — bind `ExchangeRateProperties`.
- `TODO 3d` — create a `FixedExchangeRateService` bean from the configured rates. Do not create it when the application has its own `ExchangeRateService`.
- `TODO 3e` — register the class in `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`.

**Hints:**

- `GreetingAutoConfiguration` in lesson section 3.6 is a one-to-one example.
- `@ConditionalOnBooleanProperty(name = ..., matchIfMissing = true)`, `@EnableConfigurationProperties`, `@ConditionalOnMissingBean`.
- Write the **fully qualified** class name (with the package) into the imports file.

**Acceptance criteria:** all 5 tests in `Exercise3Test` pass.

**Estimated time:** 45 minutes

# Bonus Challenge (Optional)

Split Exercise 3 into two separate Maven modules (`...-autoconfigure` and `...-spring-boot-starter`) as in lesson section 3.6, and use the starter from the lesson application. Check that your IDE auto-completes `bookstore.exchange.rates.USD`.
