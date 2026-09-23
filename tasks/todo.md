# TODO — Spring Boot Eğitim Projesi

> Kaynak: [SPEC.md](../SPEC.md) · Plan: [plan.md](plan.md)
> Kural: Görevler sırayla, tek tek yapılır (`/build`). *Verify* adımı geçmeden kutu işaretlenmez.
> Her modül için **(a) kod**, **(b) ödev** ve **(c) doküman** görevlerinin hepsi CLAUDE.md'deki *Module Definition of Done*'a tabidir.

Ortak doğrulama komutu (aşağıda `VERIFY(<id>)` olarak geçer):
```bash
./mvnw -pl modules/<id>/lesson,modules/<id>/solution -am verify && ./scripts/check-module.sh <id>
```

---

## Faz 0 — Temel

- [ ] **T0.1 Git + repo hijyeni**
  - Acceptance: `git init`, `.gitignore` (Maven, IDE, OS, `.env`), `.editorconfig`, `.gitattributes` (`mvnw` LF, `*.pdf` binary), `LICENSE` (MIT, kod) + `LICENSE-docs` (CC BY-SA 4.0)
  - Verify: `git status` temiz; `git check-ignore target/` eşleşiyor
  - Files: `.gitignore`, `.editorconfig`, `.gitattributes`, `LICENSE`, `LICENSE-docs`

- [ ] **T0.2 Java 27 araç zinciri smoke testi**
  - Acceptance: Maven Wrapper (3.9.x) JDK 27 ile çalışıyor; Boot 4.1.1 + `release 27` ile bir "hello" uygulaması derleniyor ve test ediliyor; `eclipse-temurin:27` image'ının varlığı ve GraalVM for JDK 27 durumu kontrol edilip sonuç SPEC'teki Karar 6'ya yazılıyor; kind/kubectl/helm kurulu mu kontrol ediliyor
  - Verify: `./mvnw -v` → Java 27; hello uygulaması `./mvnw verify` yeşil
  - Files: `mvnw`, `mvnw.cmd`, `.mvn/wrapper/maven-wrapper.properties`, `SPEC.md`

- [ ] **T0.3 Root aggregator + build-parent**
  - Acceptance: `build-parent` → `spring-boot-starter-parent:4.1.1`, `java.version=27`, BOM import: Spring Cloud 2025.1.3, Spring AI 2.0.1, Spring Modulith 2.1.1; enforcer (Java ≥ 27, Maven ≥ 3.9, dependency convergence), surefire (`*Test`) + failsafe (`*IT`), `-Xlint:all,-processing`; root `pom.xml` lesson + solution modüllerini, `exercises` profili exercise modüllerini içeriyor
  - Verify: `./mvnw verify` yeşil (henüz modül yok); JDK 23 ile çalıştırınca enforcer anlaşılır bir hata veriyor
  - Files: `pom.xml`, `build-parent/pom.xml`

- [ ] **T0.4 Root compose.yaml (profilli altyapı)**
  - Acceptance: `postgres:18`, `mongo:8`, `elasticsearch:9.4.x` (security kapalı, 512 MB heap), `redis:8`, `apache/kafka:4.2.x` (KRaft), `hazelcast/hazelcast:5.5`, `grafana/otel-lgtm`, `ollama/ollama` (+ pgvector'lü postgres image); hepsinde healthcheck; profiller: `postgres, mongo, elastic, redis, kafka, hazelcast, observability, ai, all` (`all` Ollama'yı içermez); şifreler `.env.example` üzerinden
  - Verify: `docker compose --profile all up -d && docker compose ps` → hepsi `healthy`
  - Files: `compose.yaml`, `.env.example`

- [ ] **T0.5 PDF hattı**
  - Acceptance: `scripts/build-pdfs.sh [modül-id]` Docker'daki `pandoc/extra` ile tüm `docs/**/*.md` dosyalarını yanlarına PDF olarak üretiyor; XeLaTeX + Türkçe karakter destekli font (ör. DejaVu / Noto); kod blokları renkli; kapak sayfası (logo, başlık, modül no, dil, tarih, lisans) YAML front matter'dan; tutarlı renk teması (başlıklar, kod blokları, not/uyarı kutuları); basit SVG logo `docs/assets/logo.svg`
  - Verify: `ç ğ ı İ ö ş ü` içeren örnek TR dokümanın PDF'i doğru görünüyor (açıp göz kontrolü)
  - Files: `scripts/build-pdfs.sh`, `docs/templates/pandoc/defaults.yaml`, `docs/templates/pandoc/header.tex`, `docs/templates/pandoc/cover.tex`, `docs/assets/logo.svg`

- [ ] **T0.6 Doküman şablonları**
  - Acceptance: `docs/templates/{tr,en}/{ders|lesson,odevler|exercises}.md` SPEC'teki 6 bölümlük yapıya uygun; module `README.md` şablonu (TR+EN)
  - Verify: Şablonlardan PDF üretiliyor
  - Files: `docs/templates/tr/ders.md`, `docs/templates/tr/odevler.md`, `docs/templates/en/lesson.md`, `docs/templates/en/exercises.md`, `docs/templates/README.module.md`

- [ ] **T0.7 Modül scaffold + DoD kontrol scriptleri**
  - Acceptance: `new-module.sh <id>` → lesson/exercise/solution Maven modülleri, docs, README, `requests.http` üretir ve root POM'a ekler. `check-module.sh <id>` → zorunlu dosyalar var; exercise ve solution test dizinleri aynı; TR/EN başlık ve kod bloğu sayısı eşit; PDF'ler MD'den yeni; `exercise/` içinde en az bir `TODO`
  - Verify: `./scripts/new-module.sh 99-sandbox && ./mvnw -pl modules/99-sandbox/lesson verify && ./scripts/check-module.sh 99-sandbox` başarılı; bozulan bir kural anlaşılır hata veriyor; sonra 99-sandbox silinir
  - Files: `scripts/new-module.sh`, `scripts/check-module.sh`, `scripts/lib.sh`

- [ ] **T0.8 CI (GitHub Actions)**
  - Acceptance: JDK 27 (temurin), Maven cache, `./mvnw verify` (Testcontainers ile), exercise'ler için `test-compile`, tüm modüllerde `check-module.sh`
  - Verify: Push sonrası workflow yeşil (remote gerekiyor — **ask first**)
  - Files: `.github/workflows/ci.yml`

- [ ] **T0.9 Kök README (TR + EN)**
  - Acceptance: Önkoşullar (JDK 27, Docker, ≥ 8 GB RAM; modül 22–23 için kind/kubectl/helm; 24 için Ollama), hızlı başlangıç, modül tablosu, ödev çözme akışı
  - Files: `README.md`

**🔶 Checkpoint 0** — plan.md'deki kriterler.

---

## Faz 1 — Pilot: `01-core-container`

- [ ] **P1.a Lesson kodu + testler**
  - Örnekler (≥ 5): DI türleri (constructor vs setter, neden field değil); `@Component` / `@Bean` ve `@Configuration` proxy'si; bean scope'ları (singleton, prototype, request) ve prototype-in-singleton tuzağı; lifecycle (`@PostConstruct`, `SmartLifecycle`); `@Profile` ve `@Conditional*`; programatik kayıt için `BeanRegistrar` (Framework 7); `ApplicationEvent` + `@EventListener` + `@TransactionalEventListener` girişi; basit `@Aspect` ile loglama
  - Verify: `./mvnw -pl modules/01-core-container/lesson verify` yeşil; `spring-boot:run` çalışıyor
- [ ] **P1.b Ödevler + çözümler** — 3 ödev: (1) strateji deseni için `Map<String, Strategy>` injection, (2) koşullu bean ile özellik bayrağı, (3) event tabanlı "kitap eklendi" bildirimi. Verify: solution yeşil, exercise derleniyor ve testleri kırmızı
- [ ] **P1.c Doküman TR/EN + PDF** — Verify: `VERIFY(01-core-container)`

**🔶 Checkpoint 1 — İNSAN ONAYI:** pilot modülün formatı onaylanmadan Faz 2'ye geçilmez. Gerekirse şablonlar (T0.6) ve scriptler güncellenir.

---

## Faz 2 — Çekirdek

### 00-setup-modern-java
- [ ] **M00.a Kod** — record + compact constructor; sealed interface + switch pattern matching; record patterns; `var` ve text blocks; virtual threads ile 10 000 görev; structured concurrency ve scoped values (API durumu T0.2'de doğrulanır; preview ise ayrı `preview` profili); Sequenced Collections; stream gatherers
- [ ] **M00.b Ödevler** — pattern matching ile fiyat hesaplayıcı; virtual thread'lerle paralel HTTP çağrısı; sealed hiyerarşi ile sipariş durum makinesi
- [ ] **M00.c Doküman** — kurulum (JDK 27, IDE, Docker), repo kullanımı, ödev akışı + Java yenilikleri. Verify: `VERIFY(00-setup-modern-java)`

### 02-configuration
- [ ] **M02.a Kod** — auto-configuration'ı inceleme (`--debug`, conditions report); `application.yaml` + profiller + config öncelik sırası; `@ConfigurationProperties` record + validation; `@Value` ve neden az kullanılmalı; config import (`optional:file:`), env var eşlemesi; **kendi starter'ını yazma** (autoconfigure + starter ayrı modül, `AutoConfiguration.imports`)
- [ ] **M02.b Ödevler** — doğrulanan özellikler ile mağaza ayarları; profile göre farklı fiyatlandırma; `bookstore-greeting-starter`
- [ ] **M02.c Doküman** — Verify: `VERIFY(02-configuration)`

### 03-web-mvc
- [ ] **M03.a Kod** — Book CRUD REST API; DTO record + Bean Validation; `@RestControllerAdvice` + `ProblemDetail`; sayfalama/sıralama; **API versioning** (header/path/media-type); content negotiation; Jackson 3 özelleştirme; springdoc OpenAPI; virtual threads açık/kapalı karşılaştırma; `MockMvcTester` testleri
- [ ] **M03.b Ödevler** — Author endpoint'leri + doğrulama; özel hata tipleri ile ProblemDetail; v1/v2 farklı response şekli
- [ ] **M03.c Doküman** — Verify: `VERIFY(03-web-mvc)`

### 04-http-clients-resilience
- [ ] **M04.a Kod** — `RestClient` (builder, hata yönetimi, interceptor); HTTP interface (`@HttpExchange`) + `@ImportHttpServices`; `WebClient` karşılaştırması; `@Retryable` + backoff, `@ConcurrencyLimit` (Framework 7 core resilience); timeout ayarları; WireMock ile testler
- [ ] **M04.b Ödevler** — dış "ISBN servisi" client'ı; retry + fallback; eşzamanlılık sınırı ile rate korunması
- [ ] **M04.c Doküman** — Verify: `VERIFY(04-http-clients-resilience)`

**🔶 Checkpoint 2**

---

## Faz 3 — Veri

### 05-data-jdbc-postgres
- [ ] **M05.a Kod** — module `compose.yaml` (postgres) + Docker Compose desteği; Flyway migration'ları (V1 şema, V2 seed); `JdbcClient` ile CRUD ve `RowMapper`/record eşleme; Spring Data JDBC aggregate'leri; `@Transactional` propagation/isolation örnekleri; Testcontainers `@ServiceConnection`
- [ ] **M05.b Ödevler** — Review tablosu + migration; batch insert; rollback senaryosu testi
- [ ] **M05.c Doküman** — Verify: `VERIFY(05-data-jdbc-postgres)`

### 06-data-jpa-postgres
- [ ] **M06.a Kod** — entity eşleme; OneToMany/ManyToMany; N+1 problemini gösterip `@EntityGraph`/fetch join ile çözme; interface/record projection; Specification + dinamik filtre; auditing (`@CreatedDate`); optimistic/pessimistic locking; Spring Data AOT repositories; `@DataJpaTest` + Testcontainers
- [ ] **M06.b Ödevler** — sipariş (Order/OrderLine) modeli; N+1'i tespit edip düzeltme (SQL sayısı testiyle); dinamik arama
- [ ] **M06.c Doküman** — Verify: `VERIFY(06-data-jpa-postgres)`

### 07-data-mongodb
- [ ] **M07.a Kod** — document modelleme (embed vs reference); `MongoRepository` + derived query; `MongoTemplate` + `Criteria`; aggregation pipeline; index yönetimi; multi-document transaction (replica set); `@DataMongoTest`
- [ ] **M07.b Ödevler** — ürün kataloğu (değişken özellikler); kategori bazlı aggregation raporu; tam metin index
- [ ] **M07.c Doküman** — Verify: `VERIFY(07-data-mongodb)`

**🔶 Checkpoint 3**

---

## Faz 4 — Cache, Arama, Mesajlaşma

### 08-redis-caching
- [ ] **M08.a Kod** — `@Cacheable/@CachePut/@CacheEvict`; Redis cache manager + TTL + JSON serileştirme; cache-aside vs write-through anlatımı; `RedisTemplate` / `StringRedisTemplate` ile veri yapıları (list, set, sorted set → "en çok satanlar"); pub/sub; Spring Session Redis; `@DataRedisTest`
- [ ] **M08.b Ödevler** — kitap detay cache'i + invalidation; sorted set ile leaderboard; basit rate limiter
- [ ] **M08.c Doküman** — Verify: `VERIFY(08-redis-caching)`

### 09-hazelcast
- [ ] **M09.a Kod** — embedded Hazelcast + auto-config; client-server (compose); `IMap`, TTL, near cache; Spring Cache ile Hazelcast; `FencedLock` ile dağıtık kilit; entry processor; iki instance ile cluster demosu
- [ ] **M09.b Ödevler** — stok rezervasyonunda dağıtık kilit; near cache performans karşılaştırması; Redis vs Hazelcast karşılaştırma raporu
- [ ] **M09.c Doküman** — Verify: `VERIFY(09-hazelcast)`

### 10-elasticsearch
- [ ] **M10.a Kod** — `@Document` mapping + analyzer (Türkçe analyzer dahil); `ElasticsearchRepository`; `NativeQuery` ile bool/full-text sorgu; highlight; aggregation (facet); PostgreSQL → ES senkronizasyonu (event ile); `@DataElasticsearchTest` + Testcontainers
- [ ] **M10.b Ödevler** — otomatik tamamlama; facet'li arama API'si; yeniden indeksleme job'ı
- [ ] **M10.c Doküman** — Verify: `VERIFY(10-elasticsearch)`

### 11-messaging-kafka
- [ ] **M11.a Kod** — `KafkaTemplate` producer; `@KafkaListener` consumer + group; JSON serde; `DefaultErrorHandler` + retry + DLT; `@RetryableTopic`; Kafka transaction; **transactional outbox** (PostgreSQL); Kafka Streams ile basit sayaç; Testcontainers Kafka
- [ ] **M11.b Ödevler** — `OrderPlaced` event'i ve stok güncelleyici consumer; zehirli mesajı DLT'ye yönlendirme; outbox relay
- [ ] **M11.c Doküman** — Verify: `VERIFY(11-messaging-kafka)`

**🔶 Checkpoint 4**

---

## Faz 5 — Platform

### 12-security
- [ ] **M12.a Kod** — `SecurityFilterChain` anatomisi; in-memory → JDBC kullanıcılar, `PasswordEncoder`; form + HTTP basic; method security (`@PreAuthorize`); JWT resource server; Spring Authorization Server ile token alma; CORS/CSRF; `spring-security-test` ile testler
- [ ] **M12.b Ödevler** — rol bazlı erişim (ADMIN/CUSTOMER); "sadece kendi siparişini gör" kuralı; JWT claim'den yetki eşleme
- [ ] **M12.c Doküman** — Verify: `VERIFY(12-security)` + `security-auditor` ajan incelemesi

### 13-reactive
- [ ] **M13.a Kod** — Mono/Flux temelleri + `StepVerifier`; WebFlux annotated + functional endpoints; R2DBC PostgreSQL; reactive MongoDB; SSE stream; backpressure; virtual threads vs reactive karşılaştırması
- [ ] **M13.b Ödevler** — reactive kitap API'si; SSE ile canlı sipariş akışı; iki kaynağı `zip` ile birleştirme
- [ ] **M13.c Doküman** — Verify: `VERIFY(13-reactive)`

### 14-testing
- [ ] **M14.a Kod** — test piramidi; slice testler karşılaştırması; `MockMvcTester`, `RestTestClient`, `WebTestClient`; `@MockitoBean`/`@MockitoSpyBean`; Testcontainers + `@ServiceConnection` + reuse; `@TestConfiguration`; test verisi (fixture/builder); ArchUnit ile mimari kuralları
- [ ] **M14.b Ödevler** — test edilmemiş bir servisi test etmek; flaky testi düzeltmek; mimari kural yazmak
- [ ] **M14.c Doküman** — Verify: `VERIFY(14-testing)`

### 15-observability
- [ ] **M15.a Kod** — Actuator endpoint'leri ve güvenliği; özel `HealthIndicator`; Micrometer counter/timer/gauge; `@Observed`; OpenTelemetry tracing + log korelasyonu; structured logging (ECS); Grafana LGTM (compose) dashboard'u
- [ ] **M15.b Ödevler** — iş metriği (sipariş sayısı); yavaş endpoint'i trace ile bulmak; özel health check
- [ ] **M15.c Doküman** — Verify: `VERIFY(15-observability)`

**🔶 Checkpoint 5**

---

## Faz 6 — İleri Konular

### 16-async-scheduling-batch
- [ ] **M16.a Kod** — `@Async` + `CompletableFuture`; virtual thread executor; `@Scheduled` (cron, fixedDelay) + ShedLock tartışması; Spring Batch 6: CSV → PostgreSQL chunk job, skip/retry, restart, job parametreleri
- [ ] **M16.b Ödevler** — gece çalışan rapor job'ı; hatalı satırları atlayan import; paralel async çağrı
- [ ] **M16.c Doküman** — Verify: `VERIFY(16-async-scheduling-batch)`

### 17-graphql-websocket
- [ ] **M17.a Kod** — GraphQL schema-first; `@QueryMapping/@MutationMapping/@SchemaMapping`; `@BatchMapping` ile N+1 çözümü; subscription; `GraphQlTester`; WebSocket/STOMP ile canlı bildirim
- [ ] **M17.b Ödevler** — Author→Books sorgusu; sipariş mutation'ı; canlı stok bildirimi
- [ ] **M17.c Doküman** — Verify: `VERIFY(17-graphql-websocket)`

### 18-modulith
- [ ] **M18.a Kod** — modül yapısı ve `ApplicationModules.verify()`; application events + `@ApplicationModuleListener`; event publication registry (JDBC); Kafka'ya event externalization; `@ApplicationModuleTest`; dokümantasyon (C4/PlantUML) üretimi
- [ ] **M18.b Ödevler** — sınır ihlalini düzeltmek; yeni modül eklemek; event ile modüller arası iletişim
- [ ] **M18.c Doküman** — Verify: `VERIFY(18-modulith)`

### 19-native-performance
- [ ] **M19.a Kod** — AOT işleme (`process-aot`); GraalVM native image (`-Pnative`); `RuntimeHints`; CDS / JVM AOT cache ile başlangıç iyileştirme; başlangıç süresi + bellek karşılaştırma script'i
- [ ] **M19.b Ödevler** — reflection kullanan kodu native'e uyarlamak; AOT cache ile ölçüm; sonuç tablosu
- [ ] **M19.c Doküman** — Verify: `VERIFY(19-native-performance)` (native build CI'da ayrı iş)

### 20-docker-deployment
- [ ] **M20.a Kod** — multi-stage Dockerfile (layered jar, non-root, JRE 27); `spring-boot:build-image` (Buildpacks); uygulama + altyapı compose; liveness/readiness probe'ları; graceful shutdown; 12-factor config; image boyut karşılaştırması
- [ ] **M20.b Ödevler** — kendi Dockerfile'ını optimize etmek; compose ile çok servisli sistem; readiness'e bağımlılık eklemek
- [ ] **M20.c Doküman** — Verify: `VERIFY(20-docker-deployment)` + `docker build` başarılı

**🔶 Checkpoint 6**

---

## Faz 7 — Dağıtık Sistemler ve AI

- [ ] **T7.0 kind altyapısı** — `scripts/kind-up.sh` / `kind-down.sh` (önkoşul kontrolü: kind, kubectl, helm; lokal registry; ingress-nginx), `kind-config.yaml`
  - Verify: `./scripts/kind-up.sh && kubectl get nodes` → Ready; `kind-down.sh` temizliyor

### 21-grpc
- [ ] **M21.a Kod** — `.proto` sözleşmesi (BookCatalog) + protobuf-maven-plugin; unary servis (`@GrpcService`); server/client/bidi streaming; client stub (`@ImportGrpcClients` / channel factory); interceptor (loglama, deadline); hata → `Status` eşleme; health + reflection; in-process testler
- [ ] **M21.b Ödevler** — yeni RPC ekleyip geriye uyumlu şema değişikliği; stream ile toplu sipariş; deadline aşımını test etmek
- [ ] **M21.c Doküman** — REST vs gRPC karşılaştırması dahil. Verify: `VERIFY(21-grpc)`

### 22-kubernetes
- [ ] **M22.a Kod/Manifest** — uygulama image'ını kind'a yükleme; Deployment, Service, ConfigMap, Secret; liveness/readiness/startup probe (Actuator); requests/limits; Kustomize base + dev/prod overlay; HPA; rolling update + rollback; PostgreSQL StatefulSet (eğitim amaçlı)
- [ ] **M22.b Ödevler** — ConfigMap değişikliğini uygulamaya yansıtmak; bozuk sürümü rollback etmek; HPA ile yük altında ölçekleme
- [ ] **M22.c Doküman** — Verify: `VERIFY(22-kubernetes)` + `kubectl apply -k` ile pod'lar Ready, `kubectl rollout status` başarılı

### 23-spring-cloud
- [ ] **M23.a Kod** — Spring Cloud Gateway (route, filter, rate limit — Redis); Config Server (Git backend) + client refresh; OpenFeign vs HTTP interface karşılaştırması; Spring Cloud LoadBalancer; Circuit Breaker (Resilience4j) + fallback; Spring Cloud Kubernetes ile ConfigMap okuma ve discovery; compose ile lokal, kind ile k8s çalıştırma
- [ ] **M23.b Ödevler** — gateway'e yeni route + auth filtresi; circuit breaker açılma testi; konfigürasyonu yeniden başlatmadan güncellemek
- [ ] **M23.c Doküman** — "Spring Cloud mu, Kubernetes native mi?" karar rehberi dahil. Verify: `VERIFY(23-spring-cloud)`

### 24-spring-ai
- [ ] **M24.a Kod** — Ollama (compose, küçük model) ; `ChatClient` + prompt şablonları; structured output → record; chat memory (JDBC); pgvector ile RAG (kitap açıklamaları, ETL pipeline); tool calling (stok sorgusu); MCP server/client girişi; testlerde mock `ChatModel`
- [ ] **M24.b Ödevler** — kitap öneri asistanı; RAG ile SSS; tool ile sipariş durumu sorgusu
- [ ] **M24.c Doküman** — model seçimi, maliyet ve güvenlik (prompt injection) notları dahil. Verify: `VERIFY(24-spring-ai)`

**🔶 Checkpoint 7**

---

## Faz 8 — Capstone ve Kapanış

- [ ] **C.1 Capstone mimari dokümanı (TR/EN)** — servisler, veri akışı, hangi teknoloji neden; ADR'ler. *Ask first:* servis sınırları (öneri: gateway + order + catalog + search, aralarında gRPC ve Kafka)
- [ ] **C.2 Sipariş + katalog** — PostgreSQL/JPA siparişler, MongoDB katalog, JWT güvenlik
- [ ] **C.3 Arama + cache** — Elasticsearch arama, Redis cache, Hazelcast stok kilidi/rate limit
- [ ] **C.4 Event akışı** — Kafka ile `OrderPlaced` → stok, arama indeksi, bildirim; outbox
- [ ] **C.5 Gateway + gRPC** — Spring Cloud Gateway girişi, order→catalog stok kontrolü gRPC ile
- [ ] **C.5b Gözlemlenebilirlik + deployment** — Dockerfile'lar, tek `compose.yaml`, LGTM, Helm chart + kind'da kurulum
- [ ] **C.6 Uçtan uca test** — Testcontainers ile tam akış: sipariş ver → event → indeks → cache
- [ ] **C.7 Capstone ödevleri + çözümler + doküman/PDF**
- [ ] **C.8 Genel müfredat** — `docs/syllabus.{tr,en}.md` + PDF (haftalık akış, önkoşullar, kazanımlar)
- [ ] **C.9 Final review** — `/review` + `/ship`; SPEC'teki 7 başarı kriterinin kontrolü

**🔶 Checkpoint 8 — Proje tamam.**
