---
title: "Module 03 — REST APIs with Web MVC"
subtitle: "Lesson Notes"
module: "03-web-mvc"
lang: en-US
date: "2026-09-23"
---

<!--
  AUTHOR NOTES (delete before publishing)
  - Section numbers must match the TR document exactly (check-module.sh compares heading counts).
  - "// Ders 3.2 / Lesson 3.2" comments in the code point to section 3.2 of this document.
  - Code snippets are copied from compiled source. Put the source right above every code block as an HTML comment
    Mark the region in the source with // tag::tag-name[] ... // end::tag-name[]; ./scripts/sync-snippets.sh copies the code.
    (content: snippet: lesson/src/main/java/com/springbootedu/<package>/File.java#tag-name) — see example 3.1 below.
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
./mvnw -pl modules/03-web-mvc/lesson spring-boot:run
```

## 3.1 ...

**Goal:** What does this example show?

<!-- snippet: lesson/src/main/java/com/springbootedu/webmvc/WebMvcApplication.java#application -->
```java
@SpringBootApplication
public class WebMvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebMvcApplication.class, args);
    }
}
```

**Run it:**

```bash
curl -s localhost:8080/api/...
```

**Expected output:**

```json
{ }
```

**Its test:** `lesson/src/test/java/com/springbootedu/webmvc/ExampleTest.java`

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
