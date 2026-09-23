---
title: "Module {{MODULE_NO}} — {{TITLE_EN}}"
subtitle: "Lesson Notes"
module: "{{MODULE_ID}}"
lang: en-US
date: "{{DATE}}"
---

<!--
  AUTHOR NOTES (delete before publishing)
  - Section numbers must match the TR document exactly (check-module.sh compares heading counts).
  - "// Ders 3.2 / Lesson 3.2" comments in the code point to section 3.2 of this document.
  - Code snippets are copied from compiled source. Put the source right above every code block as an HTML comment
    (content: snippet: lesson/src/main/java/com/springbootedu/<package>/File.java#L10-L25) — see example 3.1 below.
    check-module.sh compares these lines with the source.
  - Callouts: > [!NOTE], > [!TIP], > [!IMPORTANT], > [!WARNING], > [!CAUTION]
-->

# 1. Learning Goals

By the end of this module you will be able to:

- ...
- ...
- ...

**Prerequisites:** Module ... · **Estimated time:** ... hours

# 2. Concepts

## 2.1 ...

Explain the concept briefly and concretely; add a diagram if it helps (`../assets/diagram.svg`).

> [!NOTE]
> A short note that clarifies the concept.

# 3. Step-by-Step Examples

To run the module:

```bash
./mvnw -pl modules/{{MODULE_ID}}/lesson spring-boot:run
```

## 3.1 ...

**Goal:** What does this example show?

<!-- snippet: {{APP_SNIPPET}} -->
```java
{{APP_SNIPPET_CODE}}
```

**Run it:**

```bash
curl -s localhost:8080/api/...
```

**Expected output:**

```json
{ }
```

**Its test:** `lesson/src/test/java/com/springbootedu/{{PACKAGE}}/ExampleTest.java`

# 4. Common Mistakes and Best Practices

> [!WARNING]
> A common mistake and why it is a problem.

- **Do:** ...
- **Don't:** ...

# 5. Summary

- ...
- ...

Next step: [Exercises](exercises.md)

# 6. Further Reading

- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/reference/)
- ...
