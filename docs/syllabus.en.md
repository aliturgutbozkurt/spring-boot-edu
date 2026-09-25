---
title: "Spring Boot 4 — From the Basics to Production"
subtitle: "Course Syllabus"
module: "syllabus"
lang: en-US
date: "2026-09-25"
---

# 1. About the Course

A hands-on course on Spring Boot 4.1 and Java 27 in 25 modules and a capstone project. Every module is a small, runnable Maven project around the same domain — a bookstore (`Book`, `Author`, `Customer`, `Order`, `Review`) — so that you learn Spring, not a new domain every week.

Every module contains:

| Part | What it is |
|---|---|
| `lesson/` | runnable examples with tests — always green |
| `docs/` | lesson notes and exercises in Turkish and English (Markdown and PDF) |
| `exercise/` | starter code with `TODO`s — its tests are red until you solve them |
| `solution/` | the reference solution with the same tests — green |
| `README.md`, `requests.http` | how to run the module, HTTP examples for the IDE |

**Audience:** developers who know Java and are new to Spring, or who used Spring before and want the state of 2026 (Spring Boot 4, Spring Framework 7, Java 27).

**Format:** self-study or classroom. A week of the plan below is about 9 hours: lesson notes and examples (≈ 60 %), exercises (≈ 40 %).

# 2. Prerequisites and Setup

**Knowledge:**

- Java: classes, interfaces, generics, collections, lambdas and streams
- basic SQL (`SELECT`, `JOIN`, `INSERT`) and HTTP (methods, status codes, JSON)
- Git and a terminal

**Machine:**

| Tool | Needed for |
|---|---|
| JDK 27 | everything (the Maven wrapper brings Maven 3.9) |
| Docker with ≥ 8 GB memory | databases and brokers from module 05 on (Testcontainers, Docker Compose) |
| an IDE (IntelliJ IDEA, VS Code) | `requests.http` files, debugging |
| kind, kubectl, Helm | modules 22 and 23, the capstone on Kubernetes |
| ≈ 2 GB free disk for models | module 24 (Ollama) |

Module 00 walks through the setup; `./mvnw verify` in the repository root proves that everything works.

# 3. Learning Outcomes

After the course you will be able to:

1. build REST, GraphQL, WebSocket and gRPC APIs with Spring Boot 4, validated and with RFC 9457 error answers
2. store data in PostgreSQL (JDBC, JPA), MongoDB, Redis, Hazelcast and Elasticsearch — and choose between them
3. connect services reliably: HTTP clients with retries, Kafka with a transactional outbox and idempotent consumers
4. secure applications with Spring Security: form login, JWT resource servers, OAuth2 and method security
5. test on every level: unit tests, slice tests, Testcontainers, contract fakes, end-to-end tests
6. make applications observable (metrics, traces, logs with OpenTelemetry and Grafana) and production-ready (Docker images, native images, Kubernetes, Helm)
7. structure larger applications with Spring Modulith and Spring Cloud, and add AI features with Spring AI
8. put all of it together in a system of several services (capstone)

# 4. Weekly Plan

The order follows the module numbers; each module builds on the ones before it (see section 5).

| Week | Modules | Main topics | Hours |
|---|---|---|---|
| 1 | 00, 01 | setup, modern Java 21 → 27; IoC and dependency injection | 7 |
| 2 | 02, 03 | configuration, auto-configuration, a starter; REST with Web MVC | 9 |
| 3 | 04, 05 | `RestClient`, HTTP interfaces, `@Retryable`; JDBC, Flyway, Testcontainers | 9 |
| 4 | 06, 07 | JPA and Hibernate; MongoDB | 11 |
| 5 | 08, 09 | Redis and caching; Hazelcast (maps, locks, entry processors) | 8 |
| 6 | 10, 11 | Elasticsearch; Kafka, outbox, Kafka Streams | 11 |
| 7 | 12, 13 | Spring Security, JWT, OAuth2; reactive programming, WebFlux, R2DBC | 12 |
| 8 | 14, 15 | testing strategies; observability with OpenTelemetry and Grafana | 10 |
| 9 | 16, 17 | async, scheduling, Spring Batch; GraphQL and WebSocket | 10 |
| 10 | 18, 19 | Spring Modulith; AOT processing, the JVM AOT cache, GraalVM native image | 8 |
| 11 | 20, 21 | Docker images, jlink, Buildpacks; gRPC | 8 |
| 12 | 22, 23 | Kubernetes with kind; Spring Cloud (Gateway, Config, LoadBalancer, Circuit Breaker) | 11 |
| 13 | 24, capstone | Spring AI (chat, structured output, RAG, tools, MCP); start the capstone | 5 + 4 |
| 14 | capstone | the bookstore platform: guide, exercises, Kubernetes | 6–8 |

> [!TIP]
> Short on time? Weeks 1–8 are the core of everyday Spring work. Modules 16–24 can be taken in any order once their prerequisites are done.

# 5. Module Overview

## 5.1 Foundations

| Module | Topic | Prerequisites | Hours |
|---|---|---|---|
| 00 | Setup and modern Java (21 → 27) | basic Java | 3 |
| 01 | Spring core container: IoC and dependency injection | 00 | 4 |
| 02 | Configuration and auto-configuration | 01 | 4 |
| 03 | REST APIs with Web MVC | 01, 02 | 5 |
| 04 | HTTP clients and resilience | 01–03 | 4 |

## 5.2 Data

| Module | Topic | Prerequisites | Hours |
|---|---|---|---|
| 05 | Spring JDBC and PostgreSQL | 01–03, basic SQL | 5 |
| 06 | Spring Data JPA and Hibernate | 05 | 6 |
| 07 | Spring Data MongoDB | 05, 06 | 5 |
| 08 | Redis and caching | 01–03 | 4 |
| 09 | Distributed data with Hazelcast | 08 | 4 |
| 10 | Search with Elasticsearch | 06 | 5 |

## 5.3 Integration and Security

| Module | Topic | Prerequisites | Hours |
|---|---|---|---|
| 11 | Messaging with Kafka | 06 | 6 |
| 12 | Security with Spring Security | 03, 06 | 6 |
| 13 | Reactive programming and WebFlux | 03, 07 | 6 |
| 14 | Testing Spring Boot applications | 06 | 5 |
| 15 | Observability: metrics, traces, logs | 03 | 5 |
| 16 | Async, scheduling and Spring Batch | 06 | 5 |
| 17 | GraphQL and WebSocket | 06, 13 | 5 |
| 18 | Spring Modulith | 06, 11 | 4 |

## 5.4 Production and Beyond

| Module | Topic | Prerequisites | Hours |
|---|---|---|---|
| 19 | Native image and performance | 03, 05 | 4 |
| 20 | Docker and deployment | 15 | 4 |
| 21 | gRPC | 03 | 4 |
| 22 | Kubernetes | 20 | 5 |
| 23 | Spring Cloud | 04, 15, 22 | 6 |
| 24 | Spring AI | 06 | 5 |
| capstone | Bookstore platform: 4 services + gateway, all technologies together | all | 8–12 |

# 6. Assessment

- **Exercises:** at least three per module. An exercise is done when its tests are green — the tests are the acceptance criteria, and they are the same tests as in the solution. Each exercise names its estimated time and difficulty.
- **Capstone:** three exercises on the running platform (idempotency, cancellation with compensation, retries) plus optional challenges. The end-to-end test must stay green.
- **Suggested grading for a classroom course:** exercises 50 %, capstone exercises 30 %, a short presentation of one extra challenge 20 %.

# 7. Technology Stack

| Area | Version |
|---|---|
| Java | 27 |
| Spring Boot | 4.1.1 (Spring Framework 7.0, Spring Security 7.1, Spring Data 2026.0) |
| Spring Cloud / Spring AI / Spring Modulith | 2025.1.3 / 2.0.1 / 2.1.1 |
| Build and test | Maven 3.9 (wrapper), JUnit 6, AssertJ, Mockito, Testcontainers 2 |
| Infrastructure (Docker) | PostgreSQL 18, MongoDB 8, Elasticsearch 9, Redis 8, Kafka 4 (KRaft), Hazelcast 5.5, Ollama, Grafana LGTM |
| Deployment | Docker (jlink images), GraalVM native image, kind, Kustomize, Helm |

# 8. Tips for Learners and Teachers

- Run the lesson first (`./mvnw -pl modules/NN-…/lesson spring-boot:run`), then read the notes next to the code: every example names its lesson section.
- Do the exercises before reading the solution. The hints in each exercise point to the lesson section that helps.
- The "Common Mistakes" section of every lesson is a good starting point for classroom discussions.
- For a classroom course, start each week with the architecture of the module (section 2 of the lesson) and use the `requests.http` file for live demos.
