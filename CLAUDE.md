# CLAUDE.md — Spring Boot Education Project

A bilingual (Turkish + English), module-by-module Spring Boot course.
Every module ships **runnable example code, tests, lesson docs (MD + PDF, TR + EN), exercises and solutions**.
The source of truth for scope is [SPEC.md](SPEC.md); the build order is [tasks/plan.md](tasks/plan.md); the open work is [tasks/todo.md](tasks/todo.md).

## Workflow (Spec-Driven Development, agent-skills plugin)

1. **Spec first.** Never add a module, dependency or feature that is not in `SPEC.md`. If scope changes, update `SPEC.md` first, then `tasks/plan.md`, then `tasks/todo.md`.
2. **One task at a time** from `tasks/todo.md` (`/build`). Tick the checkbox only after its *Verify* step passes.
3. **Test-first for code** (`/test`): write the failing test, make it pass, refactor.
4. **Review before a module is "done"** (`/review`): run the Module Definition of Done below.
5. Keep changes small: one task ≈ one commit (Conventional Commits: `feat(06-data-jpa-postgres): ...`, `docs(06-data-jpa-postgres): ...`).
6. **GitHub issues:** every task/module has an issue (numbers in `tasks/todo.md`, map in `tasks/issues.json`, milestones = phases). Commits say `Refs #N`; the commit finishing a task says `Closes #N`. Module issues close only when all a/b/c items are done.

## Tech Stack (pinned — do not change without asking)

| Item | Version |
|---|---|
| Java | **27** (`maven.compiler.release=27`, no `--enable-preview` in lesson code unless the lesson is *about* a preview feature) |
| Spring Boot | **4.1.1** (Spring Framework 7.0.x, Spring Security 7.1.x, Spring Data 2026.0.x) |
| Build | Maven via wrapper (`./mvnw`), multi-module |
| Test | JUnit 6, AssertJ, Mockito, Testcontainers 2.x with `@ServiceConnection` |
| Infra (Docker) | PostgreSQL, MongoDB, Elasticsearch, Redis, Kafka (KRaft), Hazelcast, Ollama, Grafana LGTM |
| Extra BOMs | Spring Cloud 2025.1.3, Spring AI 2.0.1, Spring Modulith 2.1.1 (Spring gRPC 1.1.1 is in the Boot BOM) |
| Kubernetes | kind (local), Kustomize, Helm (capstone) |
| Docs | Markdown → PDF via Pandoc + XeLaTeX (runs in Docker, nothing to install locally) |

Library versions come from the Spring Boot BOM. Never hard-code a version that the BOM already manages.

## Commands

```bash
# JDK: Maven must run on JDK 27 (the machine default Maven JDK may be older)
export JAVA_HOME=$(/usr/libexec/java_home -v 27)

./mvnw -q verify                               # build + test everything (lessons + solutions)
./mvnw -pl modules/06-data-jpa-postgres/lesson -am verify   # one module
./mvnw -pl modules/06-data-jpa-postgres/lesson spring-boot:run   # run a lesson (starts its Docker services automatically)
./mvnw -Pexercises -pl modules/06-data-jpa-postgres/exercise test # student exercise tests (fail until solved)

docker compose --profile postgres up -d        # start infra manually (profiles: postgres, mongo, elastic, redis, kafka, hazelcast, observability, all)
docker compose down -v                         # stop + wipe volumes

./scripts/build-pdfs.sh                        # all MD → PDF (Dockerized Pandoc)
./scripts/build-pdfs.sh 06-data-jpa-postgres            # one module
./scripts/check-module.sh 06-data-jpa-postgres          # structure, TR/EN parity, snippets, fresh PDFs
./scripts/check-module.sh --strict 06-data-jpa-postgres # + finished content — required for Definition of Done
./scripts/new-module.sh 06-data-jpa-postgres --title-tr "..." --title-en "..." --infra postgres   # scaffold (id must be in SPEC)
./scripts/kind-up.sh / kind-down.sh            # local Kubernetes cluster (modules 22, 23, capstone)
```

## Repository Layout

```
pom.xml                         # root aggregator + shared plugin config
build-parent/pom.xml            # parent for all modules (Boot parent, Java 27, enforcer, test config)
compose.yaml                    # all infra services, grouped by Docker Compose profiles
docs/templates/                 # lesson/exercise templates (tr + en)
scripts/                        # build-pdfs.sh, check-module.sh, new-module.sh (logic in scripts/lib/coursetool.py)
modules/NN-slug/
  README.md                     # bilingual index: what, how to run, links to docs
  (no compose.yaml by default)   # lesson/ reuses the root compose.yaml via spring.docker.compose.profiles.active
  lesson/                       # Maven module: runnable examples + tests (always green)
  exercise/                     # Maven module: starter code with TODOs + tests (red until solved; only in -Pexercises)
  solution/                     # Maven module: reference solution; same tests as exercise/ (always green)
  docs/tr/ders.md  docs/tr/odevler.md  docs/tr/ders.pdf  docs/tr/odevler.pdf
  docs/en/lesson.md docs/en/exercises.md docs/en/lesson.pdf docs/en/exercises.pdf
capstone/                       # final project combining all technologies
tasks/plan.md, tasks/todo.md    # plan and task list
```

## Code Conventions

- groupId `com.springbootedu`; base package `com.springbootedu.<moduleslug>` (e.g. `com.springbootedu.datajpa`). Example sub-packages by *feature*, not by layer: `book/`, `order/`.
- Shared example domain across modules: **Bookstore** (`Book`, `Author`, `Customer`, `Order`, `Review`) so students do not re-learn a domain every lesson.
- Constructor injection only. No field `@Autowired`. No Lombok — use records and plain Java.
- DTOs are `record`s; validation with Jakarta Bean Validation on DTOs.
- Errors as RFC 9457 `ProblemDetail`.
- Null-safety: annotate packages with JSpecify `@NullMarked`.
- Prefer modern APIs: `RestClient`/HTTP interfaces over `RestTemplate`, `JdbcClient` over `JdbcTemplate`, `MockMvcTester`/`RestTestClient` in tests, virtual threads enabled where relevant.
- Configuration in `application.yaml`, typed with `@ConfigurationProperties` records. No secrets in files — use env vars with dev defaults only for local Docker.
- Each example class has a short Javadoc that says which lesson section it belongs to (`// Ders 3.2 / Lesson 3.2`). Comments explain *why*, in English; Turkish explanations go to the docs.
- License headers are not needed; the repo is MIT (code) + CC BY-SA 4.0 (docs).
- Code must be readable by a learner: small methods, explicit names, no clever tricks. Pedagogy beats brevity.

```java
@RestController
@RequestMapping("/api/books")
class BookController {                       // Lesson 3.1 — minimal REST controller

    private final BookService books;

    BookController(BookService books) {      // constructor injection, no @Autowired needed
        this.books = books;
    }

    @GetMapping("/{id}")
    BookResponse find(@PathVariable long id) {
        return books.find(id);               // BookNotFoundException → ProblemDetail (404)
    }
}
```

## Tests

- Every lesson example has a test. `lesson/` and `solution/` must always be green.
- Unit tests: `*Test`. Integration (Testcontainers, full context): `*IT`, run by Failsafe in `verify`.
- Prefer slice tests (`@WebMvcTest`, `@DataJpaTest`, `@DataMongoTest`, `@DataRedisTest`, `@DataElasticsearchTest`, …) before `@SpringBootTest`.
- Infra in tests always via Testcontainers + `@ServiceConnection` — never depend on a manually started container.
- Exercise tests are identical to solution tests (`scripts/check-module.sh` diffs them).
- Never delete, `@Disabled` or weaken a failing test to get green. Fix the code or ask.

## Documentation Rules

- TR and EN docs are **parallel**: same sections, same numbering, same code snippets. Changing one requires changing the other in the same commit.
- Lesson doc structure (from `docs/templates/`): Learning goals → Concepts → Step-by-step examples (linked to real source files) → Common mistakes → Summary → Further reading (official docs).
- Code snippets in docs must be copied from compiled source. Put `<!-- snippet: lesson/src/main/java/...#L10-L25 -->` (path relative to the module) directly above the code block; `check-module.sh` fails if the block and the source lines differ. Never write untested code in docs.
- Every doc starts with YAML front matter: `title`, `subtitle`, `module`, `lang` (`tr-TR` / `en-US`), `date`.
- Callouts use GitHub alert syntax (`> [!NOTE]`, `[!TIP]`, `[!IMPORTANT]`, `[!WARNING]`, `[!CAUTION]`) — rendered natively on GitHub and as coloured boxes in the PDF. Do not use raw LaTeX in Markdown.
- Pipeline self-test: `./scripts/build-pdfs.sh --force docs/templates/pandoc/samples` (TR + EN sample exercising every feature).
- Regenerate the PDFs after any MD change (`./scripts/build-pdfs.sh <module>`) and commit them together.
- Cite official Spring docs for version-specific claims; if unsure whether a feature exists in Boot 4.1, verify in docs before writing about it.

## Module Definition of Done

- [ ] `lesson/` runs with one command and all its tests pass
- [ ] `solution/` tests pass; `exercise/` compiles and its tests fail only on TODOs
- [ ] `docs/tr` and `docs/en` lesson + exercises MD written, parallel, PDFs regenerated
- [ ] `README.md` has run instructions (with and without Docker already running)
- [ ] Infra, if any, started from the root `compose.yaml` profiles on `spring-boot:run` and covered by Testcontainers in tests
- [ ] `./scripts/check-module.sh --strict NN-slug` passes
- [ ] Checkbox ticked in `tasks/todo.md`

## Boundaries

- **Always:** run `./mvnw verify` for the touched module before claiming done; keep TR/EN in sync; use BOM-managed versions; keep every module independently runnable.
- **Ask first:** adding a dependency not in SPEC.md; changing Java/Spring Boot version; adding/removing/renumbering modules; changing the shared domain model; changing CI.
- **Never:** commit secrets or real credentials; use Lombok or field injection; commit solution code into `exercise/`; skip/disable tests to get green; put untested code in docs; use deprecated APIs in lesson code except in an explicit "old vs new" comparison.

## Gotchas

- `mvn -v` on this machine reports JDK 23 — always set `JAVA_HOME` to 27 (see Commands) or builds fail with "release 27 not supported".
- Boot 4 is modular: `@ServiceConnection` needs the technology's starter on the classpath (e.g. `spring-boot-starter-jdbc` for a Postgres container). Driver alone → `ConnectionDetailsNotFoundException`.
- `StructuredTaskScope` is still preview in Java 27; only module 00's `preview` profile may use it.
- Container base image is `amazoncorretto:27-alpine` (no Temurin 27 yet). Native images use GraalVM 25 — only in module 19.
- `docker compose up` failing with "No such image" right after a pull means the Docker VM disk is nearly full and images get evicted — free space (`docker builder prune`) before debugging anything else.
- Elasticsearch needs ≥ 2 GB Docker memory; Kafka runs in KRaft mode (no ZooKeeper).
- Spring AI tests never call a real LLM: use a mocked `ChatModel` or Testcontainers Ollama with a tiny model, tagged `*IT`.
- Spring Boot's Docker Compose support starts services on `spring-boot:run`; in tests it is disabled — Testcontainers is used instead.
