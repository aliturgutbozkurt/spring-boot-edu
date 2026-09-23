---
title: "Module {{MODULE_NO}} — {{TITLE_EN}}"
subtitle: "Exercises"
module: "{{MODULE_ID}}"
lang: en-US
date: "{{DATE}}"
---

# How to Work

1. The starter code is in `modules/{{MODULE_ID}}/exercise/`. Find the `TODO` comments.
2. Every exercise already has tests, and they stay **red** until you solve it.
3. Run the tests:

```bash
./mvnw -Pexercises -pl modules/{{MODULE_ID}}/exercise test
```

4. The exercise is done when all tests are green.
5. If you get stuck, read the hints first, then the solution in `modules/{{MODULE_ID}}/solution/`.

> [!TIP]
> To run the tests of a single exercise: `-Dtest=Exercise1Test`

# Exercise 1 — ... (Easy)

**Goal:** ...

**Tasks:**

- `TODO 1` in `exercise/src/main/java/com/springbootedu/{{PACKAGE}}/...` ...

**Hints:**

- See lesson section 3.1.

**Acceptance criteria:** all tests in `Exercise1Test` pass.

**Estimated time:** 20 minutes

# Exercise 2 — ... (Medium)

**Goal:** ...

**Tasks:**

- ...

**Hints:**

- ...

**Acceptance criteria:** all tests in `Exercise2Test` pass.

**Estimated time:** 40 minutes

# Exercise 3 — ... (Hard)

**Goal:** ...

**Tasks:**

- ...

**Hints:**

- ...

**Acceptance criteria:** all tests in `Exercise3Test` pass.

**Estimated time:** 60 minutes

# Bonus Challenge (Optional)

An open-ended extension idea without tests.
