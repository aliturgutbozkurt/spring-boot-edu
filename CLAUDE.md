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
./scripts/sync-snippets.sh 06-data-jpa-postgres         # refresh doc code blocks from // tag:: regions in the source
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
- Exercise tests are identical to solution tests (`scripts/check-module.sh` diffs them). Exception (SPEC decision 11): in `14-testing` students write the tests — there `src/main` is identical, TODOs are in `exercise/src/test`, and mutation/meta tests prove the solution tests catch bugs.
- Never delete, `@Disabled` or weaken a failing test to get green. Fix the code or ask.

## Documentation Rules

- TR and EN docs are **parallel**: same sections, same numbering, same code snippets. Changing one requires changing the other in the same commit.
- Lesson doc structure (from `docs/templates/`): Learning goals → Concepts → Step-by-step examples (linked to real source files) → Common mistakes → Summary → Further reading (official docs).
- Code snippets in docs come from compiled source. Mark the region in the source with `// tag::name[]` … `// end::name[]`, put `<!-- snippet: lesson/src/main/java/.../File.java#name -->` (path relative to the module) directly above the code block, and run `./scripts/sync-snippets.sh <module>` to copy the code in. `check-module.sh` fails when a block and its source differ. Prefer tags over `#L10-L25` line ranges (they break when code moves). Never write untested code in docs.
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
- `new-module.sh` registers lesson/, solution/ and exercise/ in the root pom at once. Commit the root `pom.xml` together with the module's first commit, and the scaffolded `solution/` and `exercise/` with it (even as placeholders). Missing pom → CI silently skips the module; missing folders → CI fails. Run `git status` before every commit.
- A module with extra Maven modules (e.g. 02's starter) must be run with `-am`: `./mvnw -pl modules/<id>/lesson -am spring-boot:run`. build-parent's `not-an-application` profile skips `spring-boot:run` in projects without `src/main/java`, so `-am` is always safe.
- Boot registers its `ConditionEvaluationReport` as a bean named `autoConfigurationReport` — don't reuse that bean name.
- WireMock in tests: start ONE `WireMockServer` per JVM (static field + static block) and register its URL with `@DynamicPropertySource`. A per-class JUnit extension restarts it on a new port while Spring's cached context keeps the old one → "Connection refused".
- Boot picks the RestClient HTTP library from the classpath (Apache > Jetty > Reactor Netty > JDK). Adding the WebClient starter silently moves RestClient onto Netty — pin it with `spring.http.clients.imperative.factory: jdk` when that is not wanted.
- Tests that change shared in-memory state (repositories, caches, counters) must not depend on test order: use `@DirtiesContext` on the mutating test or set up the state inside the test.
- Testcontainers: declare the container as a `static final` field in a `@TestConfiguration` and return it from a `@Bean @ServiceConnection` method → one container per JVM for all cached contexts. The same database is then shared by all test classes: `@JdbcTest`/`@DataJdbcTest` roll back, but `@SpringBootTest` tests commit — clean up in `@BeforeEach` **and** `@AfterEach`, or scope assertions to their own rows.
- PostgreSQL: after one failing statement the whole transaction is aborted (`25P02`), so a test can check only one constraint violation per transaction. `avg()` returns `numeric` (cast with `::float8` for `Double`) and NULL for no rows.
- Custom Docker images (e.g. `pgvector/pgvector`) need the label `org.springframework.boot.service-connection: postgres` for Boot's Docker Compose support, and `DockerImageName...asCompatibleSubstituteFor("postgres")` for Testcontainers.
- MongoDB (Boot 4): connection settings are `spring.mongodb.*` (the old `spring.data.mongodb.uri/database/...` are deprecated); Spring Data settings stay under `spring.data.mongodb.*`. `BigDecimal` is stored as `Decimal128` by default in the Spring Data MongoDB of Boot 4.1 (older versions used strings); we still set `spring.data.mongodb.representation.big-decimal: decimal128` explicitly. Boot registers no MongoDB transaction manager — declare a `MongoTransactionManager` bean.
- Testcontainers 2 `org.testcontainers.mongodb.MongoDBContainer` does NOT start a replica set by default: call `.withReplicaSet()` for transactions. Do not name the container `@Bean` method `mongo` — Boot's `MongoClient` bean already has that name.
- `RestTestClient` / `@AutoConfigureRestTestClient` come with `spring-boot-starter-webmvc-test` (module `spring-boot-resttestclient`), not with `spring-boot-starter-restclient-test`.
- Never make methods `final` on beans that get proxied (`@Repository`, `@Transactional`, `@Cacheable`, …): the proxy cannot override them, so they run on the empty proxy instance (fields are `null`).
- Spring Data Redis 4 + Lettuce writes the cache **asynchronously** by default (`put`/`evict`/`clear` return before Redis acks) → read-after-write races. Build the writer with `RedisCacheWriter.create(factory, w -> w.immediateWrites())` in a `RedisCacheManagerBuilderCustomizer` (no Boot property). Use `Cache.invalidate()` in test setup.
- A locally installed Redis (Homebrew) on `127.0.0.1:6379` shadows the compose port — `lsof -iTCP:6379` before trusting `redis-cli` output.
- Never gate a commit on `grep` of Maven output — check the exit code (`mvn … && git commit …`).
- Hazelcast 5.5 Community: the CP Subsystem (`FencedLock`, `IAtomicLong`, …) is Enterprise-only and throws `UnsupportedOperationException`. Use `IMap.lock/tryLock` and `EntryProcessor` (SPEC decision 10).
- Hazelcast in tests: Boot's Testcontainers support accepts a `GenericContainer` of `hazelcast/hazelcast`, but only with `@ServiceConnection(name = "hazelcast/hazelcast")`; it reads `HZ_CLUSTERNAME` for the client. An `EntryProcessor` runs on the member, so client-server needs its class on the server — lesson/tests use an embedded member for it.
- Hazelcast tests share one JVM with the embedded members of cached Spring contexts: never call `Hazelcast.shutdownAll()` (shut down only your own members), and give hand-made clusters their own port (`setPort(5801)` + explicit `127.0.0.1:5801`, `:5802` members) — `addMember("127.0.0.1")` only scans 5701–5703. Test with `-Dsurefire.runOrder=alphabetical` and `reversealphabetical`.
- Docker Compose support skips `docker compose up` when ANY service of the project already runs (`start.skip: if-running`) → a module's second service (e.g. Elasticsearch next to an already running Postgres) never starts. All lesson yamls set `spring.docker.compose.start.skip: never` (idempotent `up`).
- All modules share the compose `bookstore` database: each module uses its own schema (`spring.flyway.schemas: <schema>` + `spring.datasource.hikari.schema: <schema>`), otherwise Flyway histories collide (checksum mismatch). Flyway then adds a `<< Flyway Schema Creation >>` row with `version = null`.
- Kafka (Boot 4.1): no Docker Compose service connection for Kafka — the default `spring.kafka.bootstrap-servers=localhost:9092` matches the compose HOST listener. Testcontainers: `org.testcontainers.kafka.KafkaContainer("apache/kafka:…")` + `@ServiceConnection`. JSON serdes are `JacksonJsonSerializer/Deserializer` (Jackson 3); without a web starter add `spring-boot-starter-jackson` for a `JsonMapper` bean. A `KafkaTemplate` bean disables Boot's default template — build extra (e.g. transactional) templates inside the component. `@RetryableTopic` topics are suffixed with the delay (`-retry-500`) unless configured. Every cached test context starts its own Kafka Streams instance: give tests a unique `spring.kafka.streams.state-dir` (`${random.uuid}`), and delete stale `$TMPDIR/kafka-streams/<app-id>` after switching brokers.
- Spring Security 7 / Boot 4.1: starters are `spring-boot-starter-security-oauth2-{authorization-server,resource-server,client}`; the Authorization Server lives in Spring Security (`http.oauth2AuthorizationServer(…)` / `new OAuth2AuthorizationServerConfigurer()`). Defining any `SecurityFilterChain` switches off Boot's default chains, so declare the AS chain yourself; Boot still provides registered clients (`spring.security.oauth2.authorizationserver.client.*`), the JWK source and the `JwtDecoder`. Authentications carry `FACTOR_PASSWORD` / `FACTOR_BEARER` authorities.
- Boot's property binder keeps an unresolvable `${ENV_VAR}` as literal text instead of failing — for a required secret check it explicitly (`environment.getRequiredProperty(...)`). Demo credentials go into a `dev` profile + `db/demo` Flyway location; set `<spring-boot.run.profiles>dev</spring-boot.run.profiles>` as a pom property (plugin `<profiles>` config cannot be overridden with `-D`).
- R2DBC (Boot 4.1): the Testcontainers service connection needs `org.testcontainers:testcontainers-r2dbc` on the test classpath; Flyway still runs over JDBC (add the JDBC driver). `spring.r2dbc.properties.*` is ignored when connection details come from a service connection — set driver options such as the PostgreSQL `schema` with a `ConnectionFactoryOptionsBuilderCustomizer`. A `@Configuration` class `XRoutes` and a `@Bean` method `xRoutes()` clash (same bean name).
- WebFlux SSE: the response is committed with the first element — send an initial `ServerSentEvent` comment so clients (and `WebTestClient.exchange()`) get the headers before the first real event.
- Observability (Boot 4.1): `spring-boot-starter-opentelemetry` exports metrics + traces via OTLP; Compose and Testcontainers (`LgtmStackContainer`, module `testcontainers-grafana`) provide the endpoints for `grafana/otel-lgtm`. Tests switch export off — re-enable with `spring.test.metrics.export=true` / `spring.test.tracing.export=true`. ECS log lines carry `traceId`/`spanId` as top-level fields. Logs reach Loki only with an OpenTelemetry Logback appender (not in the Boot BOM — not used). OTLP adds unit suffixes: `bookstore_orders_placed_total`, `…_milliseconds_count`. Compose publishes only Grafana (3000) and OTLP ports; query Prometheus through Grafana's datasource proxy.
- Spring Batch 6 (Boot 4.1): `spring-boot-starter-batch-jdbc` for a JDBC job repository (`spring.batch.jdbc.initialize-schema: always` for PostgreSQL); packages moved — `org.springframework.batch.core.job.{Job,JobExecution}`, `…core.step.Step`, readers/writers in `org.springframework.batch.infrastructure.item…`. Use `new StepBuilder(name, repo).<I,O>chunk(n).transactionManager(tm)` (ChunkOrientedStepBuilder: `faultTolerant().skip(..).skipLimit(..).retry(..).retryLimit(..)`), start with `JobOperator.start(job, params)`, restart with `JobOperator.restart(execution)`; tests: `@SpringBatchTest` → `JobOperatorTestUtils`. Writers with records: use `itemSqlParameterSourceProvider`, not `beanMapped()`.
- Spring Modulith 2.1.1 brings ArchUnit 1.4.2, which cannot read Java 27 class files ("No classes found in packages"). build-parent pins `com.tngtech.archunit:archunit` to `${archunit.version}` — keep it.
- Modulith's Kafka externalization serializes events to JSON itself (`spring.modulith.events.kafka.enable-json`, default true). Do not set a JSON `value-serializer`: the event arrives base64-encoded (type `[B`). Create the target topic with a `NewTopic` bean, or the first consumer waits ~40 s for metadata.
- Modulith + shared Testcontainers: on shutdown the publication registry queries the DB, but the first closed test context already stopped the shared container → surefire hangs 30 s ("kill self fork JVM"). Module 18's `TestcontainersConfiguration` sets `spring.datasource.hikari.connection-timeout=500` (ms, a plain number) via a `DynamicPropertyRegistrar` bean.
- `@ApplicationModuleTest` also loads the root package (`LessonTour`): add `@TestPropertySource(properties = "bookstore.tour.enabled=false")`.
- `ApplicationModules.of(App.class)` is cached per JVM and `verify()` marks it verified *before* throwing: after any `@ApplicationModuleTest` ran, a later `verify()` passes silently. Tests that must fail on violations use `detectViolations().throwIfPresent()`.
- Module 19 (native): `process-aot` builds the context without a database → Spring Data JDBC AOT repositories need an explicit `JdbcDialect` bean (else "Failed to determine a suitable driver class"). The Paketo native build (`-Pnative spring-boot:build-image`) needs ~4 GB free in Docker: stop the other compose services first (exit 137 = OOM). Native images contain only the `en` locale: `-H:IncludeLocales` in a `META-INF/native-image/.../native-image.properties`. CI native job (`native.yml`, manual) runs Maven on JDK 27 with `GRAALVM_HOME` = GraalVM 25.
- Paketo buildpacks have no Java 27 JRE (Liberica, Corretto 9.7.0): module 20 is compiled with `release 25` and `BP_JVM_VERSION=25` (SPEC decision 12); its Dockerfile still uses a jlink JRE 27. On `amazoncorretto:27-alpine`, `jlink --strip-debug` needs `apk add binutils` (objcopy).
- gRPC (Boot 4.1 built-in, Spring gRPC 1.1.1): `@GrpcAdvice` classes and their `@GrpcExceptionHandler` methods must be `public` (reflective call → `IllegalAccessException`, status UNKNOWN). A logging `@GlobalServerInterceptor` needs `@Order(HIGHEST_PRECEDENCE)` to see calls closed by the error mapping. Tests: `@AutoConfigureTestGrpcTransport` maps every channel in-process; don't `@ImportGrpcClients` a stub type the app already imports (duplicate beans).
- Kubernetes (module 22, kind): `scripts/kind-up.sh` creates cluster `bookstore` + metrics-server (`--kubelet-insecure-tls`). Load local images with `kind load docker-image`, `imagePullPolicy: IfNotPresent`. A Kustomize `namespace:` in the base does NOT cover resources an overlay adds (hpa.yaml) → set `namespace:` in every overlay (ManifestPolicyTest checks it). The app needs an initContainer waiting for `pg_isready`, else Flyway fails and the pod restarts.
- Spring Cloud 2025.1.3 runs on Boot 4.1.1. A Config client needs `spring.config.import` (`optional:configserver:…`); tests set it to "" via `@DynamicPropertySource`. Gateway 5 (WebFlux) properties live under `spring.cloud.gateway.server.webflux.*`; rate-limit tests need their own key (header `X-Customer`), token buckets are shared. Config Server rejects symbolic-link paths (macOS `/var` → use `toRealPath()` for temp Git repos and `git.basedir`).
- Spring Cloud Kubernetes (module 23): `loadbalancer.mode: POD` for client-side balancing over pods (SERVICE mode sticks to one pod with keep-alive); set `SPRING_CLOUD_CONFIG_ENABLED=false` as env — the base `optional:configserver:` import runs before profile documents. The pod needs RBAC get/list/watch on configmaps, services, endpoints(slices), pods.
- Building images in tests: Testcontainers `ImageFromDockerfile` (legacy build API) fails on the GitHub runner ("failed to export image … layer does not exist"), also with no-cache. Build with `ComposeContainer` + a compose file (`build:` + a tagged `image:`) instead — that path works in CI and locally.
- Spring AI 2.0.1: in tests set `spring.ai.model.chat=none` / `spring.ai.model.embedding=none` and provide fakes (a `ChatModel` must override `getOptions()` → `ToolCallingChatOptions`, else no tools are passed; `getDefaultOptions()` is deprecated for removal). Small Ollama models break `.entity(...)` JSON — use `AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT`. MCP server: set `spring.ai.mcp.server.protocol: STREAMABLE` explicitly, else no `/mcp` endpoint. `TextReader` overwrites the `source` metadata (use another key for delete filters).
- Elasticsearch needs ≥ 2 GB Docker memory; Kafka runs in KRaft mode (no ZooKeeper).
- Spring AI tests never call a real LLM: use a mocked `ChatModel` or Testcontainers Ollama with a tiny model, tagged `*IT`.
- Spring Boot's Docker Compose support starts services on `spring-boot:run`; in tests it is disabled — Testcontainers is used instead.
