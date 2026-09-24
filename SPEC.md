# Spec: Spring Boot Eğitim Projesi (Eylül 2026)

> Durum: **ONAYLANDI** (2026-09-23). Değişiklikler önce bu dosyaya yazılır.
> Son güncelleme: 2026-09-23

## Varsayımlar (onaylandı)

1. **Build aracı Maven** (wrapper ile, multi-module). Gradle kullanılmayacak.
2. **Spring Boot 4.1.1** — bugün itibarıyla en güncel GA sürüm (start.spring.io'dan doğrulandı). 4.2.0 Kasım 2026'da GA olunca yükseltme ayrı bir görev olacak.
3. **Java 27** (non-LTS, 2026-09-15). Ders kodunda preview özellikler kapalı; yalnızca "Modern Java" dersinde ayrı bir profilde gösterilir.
4. **Hedef kitle:** Java bilen, Spring'e yeni ya da orta seviye geliştiriciler. Başlangıç seviyesinde Java dersi verilmez; sadece Java 21→27 yenilikleri özetlenir.
5. **Ortak örnek domain: Kitapçı (Bookstore)** — Book, Author, Customer, Order, Review. Tüm modüller aynı domain üzerinde ilerler.
6. **Base package / groupId:** `com.springbootedu` → paketler `com.springbootedu.<modül>`.
7. **PDF'ler** Markdown'dan Pandoc + XeLaTeX ile (Docker içinde) üretilir ve **repoya commit edilir** (öğrenci araç kurmadan okuyabilsin).
8. **Ödevler** her modülde `exercise/` (TODO'lu başlangıç kodu + testler) ve `solution/` (referans çözüm + aynı testler) olarak verilir. Öğrenci, testleri yeşile çevirerek ödevi bitirir.
9. **Lisans:** kod MIT, dokümanlar CC BY-SA 4.0.
10. **Frontend yok** — sadece REST/GraphQL/WebSocket API'leri; test için `.http` dosyaları ve curl örnekleri.
11. Git reposu ve GitHub Actions CI kurulacak (dizin henüz git reposu değil).

## Amaç

Spring Boot'un güncel (4.1) özelliklerini **bol çalışan örnekle**, modül modül, Türkçe ve İngilizce anlatan; her modülde ders notu (MD + PDF), çalıştırılabilir kod, testler, ödevler ve çözümler içeren bir eğitim reposu.

**Kullanıcı hikâyeleri**
- Öğrenci olarak, repoyu klonlayıp tek bir komutla herhangi bir dersin kodunu çalıştırabilmek istiyorum; gerekli veritabanı/altyapı Docker ile kendiliğinden kalksın.
- Öğrenci olarak, dersi Türkçe ya da İngilizce, çevrimdışı PDF olarak okuyabilmek istiyorum.
- Öğrenci olarak, ödevimi yaptığımı testleri çalıştırarak doğrulayabilmek, takıldığımda çözüme bakabilmek istiyorum.
- Eğitmen olarak, her dersi bağımsız anlatabilmek ve bitirme projesinde tüm teknolojileri birlikte gösterebilmek istiyorum.

## Tech Stack

| Bileşen | Sürüm / Not |
|---|---|
| Java | 27 (JDK: Oracle/any OpenJDK 27; container: `amazoncorretto:27-alpine`) |
| Maven | 3.9.16 via Maven Wrapper 3.3.4 (Maven 4 henüz RC — kullanılmıyor) |
| Spring Boot | 4.1.1 → Spring Framework 7.0.9, Spring Security 7.1.1, Spring Data 2026.0.1, Spring Kafka 4.1.1 |
| Hibernate / Validator | 7.4.5 / 9.1.3 (BOM) |
| Flyway | 12.4.0 (BOM) |
| Test | JUnit 6.0.3, AssertJ, Mockito, Testcontainers 2.0.5, ArchUnit 1.5.0 (14-testing; BOM dışı, build-parent yönetir) |
| PostgreSQL | Docker `postgres:18` (JDBC driver 42.7.13) |
| MongoDB | Docker `mongo:8` (driver 5.8.1) |
| Elasticsearch | Docker `elasticsearch:9.4.x` (client 9.4.5 ile uyumlu) |
| Redis | Docker `redis:8` (Lettuce 7.5.2) |
| Kafka | Docker `apache/kafka:4.2.x` KRaft modu (client 4.2.1) |
| Hazelcast | 5.5.0 (embedded + Docker `hazelcast/hazelcast:5.5` client-server) |
| Spring Cloud | 2025.1.3 release train (Gateway, Config, OpenFeign, LoadBalancer, Circuit Breaker/Resilience4j, Kubernetes) |
| gRPC | Spring gRPC 1.1.1 (Boot BOM), grpc-java 1.83.1, protobuf 4.35.1 |
| Spring AI | 2.0.1, model: **Ollama** (lokal, Docker `ollama/ollama`), vektör DB: PostgreSQL pgvector |
| Spring Modulith | 2.1.1 |
| Kubernetes | Lokal cluster: **kind**; `kubectl` + Kustomize manifestleri, Helm chart (capstone) |
| Gözlemlenebilirlik | Micrometer 1.17, OpenTelemetry, Docker `grafana/otel-lgtm` |
| Doküman | Pandoc + XeLaTeX (Docker `pandoc/extra`), Türkçe karakter destekli font, **kapak sayfası + logo + renk teması** |

Kütüphane sürümleri Spring Boot BOM'undan gelir; BOM'un yönettiği sürüm elle yazılmaz.

## Capability Map (Modül Haritası)

Modül id'leri sabittir, sonradan yeniden adlandırılmaz.

| # | Modül id | Kapsam (öne çıkan konular) | Altyapı | Bağımlı olduğu |
|---|---|---|---|---|
| 00 | `00-setup-modern-java` | Kurulum, Maven wrapper, Docker, Java 21→27 yenilikleri (record, sealed, pattern matching, virtual threads, structured concurrency, scoped values) | — | — |
| 01 | `01-core-container` | IoC/DI, bean yaşam döngüsü, scope'lar, `@Conditional`, `@Profile`, `BeanRegistrar` (Framework 7), event'ler, AOP temeli | — | 00 |
| 02 | `02-configuration` | Auto-configuration nasıl çalışır, externalized config, `@ConfigurationProperties` + validation, profiller, kendi starter'ını yazmak | — | 01 |
| 03 | `03-web-mvc` | REST controller, validation, `ProblemDetail`, content negotiation, **API versioning** (Framework 7), Jackson 3, OpenAPI (springdoc), virtual threads | — | 02 |
| 04 | `04-http-clients-resilience` | `RestClient`, HTTP interface client'lar (`@ImportHttpServices`), `WebClient`, `@Retryable` / `@ConcurrencyLimit` (Framework 7 core resilience) | WireMock (test) | 03 |
| 05 | `05-data-jdbc-postgres` | `JdbcClient`, Flyway migration, Spring Data JDBC, transaction yönetimi | PostgreSQL | 02 |
| 06 | `06-data-jpa-postgres` | JPA/Hibernate 7, ilişkiler, N+1 ve fetch stratejileri, projection, Specification/Query by Example, auditing, locking, Spring Data AOT repositories | PostgreSQL | 05 |
| 07 | `07-data-mongodb` | Spring Data MongoDB, document modelleme, aggregation, index, transaction | MongoDB | 02 |
| 08 | `08-redis-caching` | Spring Cache abstraction, Redis cache, TTL, `RedisTemplate`, Redis veri yapıları, pub/sub, Spring Session | Redis (yavaş kaynak bilerek bellek içi simüle edilir; cache etkisi ölçülebilir) | 06 |
| 09 | `09-hazelcast` | Embedded vs client-server, `IMap`, near cache, `IMap` kilidi + `EntryProcessor` (bkz. karar 10), Hazelcast ile cache | Hazelcast, PostgreSQL | 08 |
| 10 | `10-elasticsearch` | Spring Data Elasticsearch, mapping, full-text search, aggregation, highlight, PostgreSQL'den senkronizasyon | Elasticsearch, PostgreSQL | 06 |
| 11 | `11-messaging-kafka` | Producer/consumer, JSON serileştirme, consumer group, retry + DLT, Kafka transaction, transactional outbox, Kafka Streams girişi | Kafka, PostgreSQL | 06 |
| 12 | `12-security` | Spring Security 7: filter chain, form/basic, parola saklama, method security, JWT resource server, OAuth2 login, Authorization Server, CORS/CSRF | PostgreSQL | 03, 06 |
| 13 | `13-reactive` | Reactor temelleri, WebFlux, functional endpoints, R2DBC (PostgreSQL), reactive MongoDB, SSE; ne zaman reactive, ne zaman virtual threads | PostgreSQL, MongoDB | 03, 07 |
| 14 | `14-testing` | Test piramidi, slice testler, `MockMvcTester`, `RestTestClient`, Testcontainers + `@ServiceConnection`, test fixture'ları, `@MockitoBean` | Tümü (Testcontainers) | 06 |
| 15 | `15-observability` | Actuator, Micrometer metrics, `@Observed`, tracing (OpenTelemetry), structured logging (ECS/Logstash), Grafana LGTM | LGTM stack | 03 |
| 16 | `16-async-scheduling-batch` | `@Async`, virtual threads ile executor, `@Scheduled`, Spring Batch 6 (chunk, reader/writer, restart) | PostgreSQL | 06 |
| 17 | `17-graphql-websocket` | Spring for GraphQL (schema-first, DataLoader, subscription), WebSocket/STOMP | PostgreSQL | 06 |
| 18 | `18-modulith` | Spring Modulith: modül sınırları, application event'ler, event publication registry, modül testleri, dokümantasyon üretimi | PostgreSQL | 06, 11 |
| 19 | `19-native-performance` | AOT işleme, GraalVM native image, CDS / JVM AOT cache (Project Leyden), başlangıç süresi karşılaştırması | — | 03 |
| 20 | `20-docker-deployment` | Dockerfile (multi-stage, layered jar), Buildpacks (`spring-boot:build-image`), Docker Compose, health probe'lar, graceful shutdown, 12-factor | Tümü | 15 |
| 21 | `21-grpc` | Spring gRPC: protobuf ile sözleşme, unary + streaming servisler, client stub'ları, interceptor, hata eşleme (status), güvenlik, test | — | 03 |
| 22 | `22-kubernetes` | kind ile lokal cluster, Deployment/Service/ConfigMap/Secret, probe'lar, kaynak limitleri, Kustomize overlay'leri, HPA, rolling update | kind | 20 |
| 23 | `23-spring-cloud` | Spring Cloud Gateway, Config Server, OpenFeign vs HTTP interface, LoadBalancer, Circuit Breaker (Resilience4j), Spring Cloud Kubernetes (ConfigMap/discovery) | kind, PostgreSQL | 04, 15, 22 |
| 24 | `24-spring-ai` | Spring AI: ChatClient, prompt şablonları, structured output, chat memory, RAG (pgvector), tool calling, MCP girişi — model Ollama ile lokal | Ollama, PostgreSQL (pgvector) | 06 |
| — | `capstone` | **Bitirme projesi — Kitapçı platformu:** sipariş (PostgreSQL/JPA), katalog (MongoDB), arama (Elasticsearch), cache (Redis), dağıtık lock (`IMap` kilidi)/rate-limit (Hazelcast), sipariş event'leri (Kafka), güvenlik (JWT), API Gateway (Spring Cloud), servisler arası gRPC, gözlemlenebilirlik; Docker Compose ile tek komutla, ayrıca kind üzerinde Kubernetes (Helm) ile ayağa kalkar | Tümü | tümü |

**Sıralama:** 00 → 01 → … → 20 → 21 → 22 → 23 → 24 → capstone
(Numara = önerilen öğrenme sırası. 07, 15, 19, 21, 24 gibi modüller bağımlılıkları bitince paralel yazılabilir.)

## Her Modülün Standart İçeriği

```
modules/NN-slug/
  README.md                  # TR + EN kısa özet, çalıştırma komutları, doküman linkleri
  (compose.yaml yok)         # lesson/ kök compose.yaml'ı profil ile kullanır (spring.docker.compose.profiles.active); özel ihtiyaç varsa modül compose.yaml'ı eklenir
  requests.http              # IDE'den çalıştırılabilir HTTP örnekleri
  lesson/                    # çalışan örnek kod + testler (her zaman yeşil)
  exercise/                  # TODO'lu başlangıç kodu + testler (çözülene kadar kırmızı)
  solution/                  # referans çözüm + exercise ile aynı testler (yeşil)
  docs/tr/ders.md, odevler.md, ders.pdf, odevler.pdf
  docs/en/lesson.md, exercises.md, lesson.pdf, exercises.pdf
```

**Ders dokümanı şablonu (TR/EN paralel):**
1. Öğrenme hedefleri
2. Kavramlar (gerekirse diyagram)
3. Adım adım örnekler — her örnek gerçek bir kaynak dosyaya bağlı, çalıştırma komutu ve beklenen çıktıyla
4. Sık yapılan hatalar / en iyi pratikler
5. Özet
6. İleri okuma (resmî dokümanlar)

**Ödev dokümanı:** Her modülde en az 3 ödev (kolay → zor), her biri: hedef, ipuçları, kabul kriterleri (hangi testler geçmeli), tahmini süre.

**Minimum örnek yoğunluğu:** Her modülde ≥ 5 çalıştırılabilir örnek, her örneğin en az bir testi.

## Commands

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 27)
./mvnw verify                                            # tüm lesson + solution modülleri
./mvnw -pl modules/06-data-jpa-postgres/lesson -am verify
./mvnw -pl modules/06-data-jpa-postgres/lesson spring-boot:run
./mvnw -Pexercises -pl modules/06-data-jpa-postgres/exercise test
docker compose --profile all up -d                       # tüm altyapı
./scripts/build-pdfs.sh [modül-id]
./scripts/check-module.sh <modül-id>
./scripts/new-module.sh <modül-id>
cd capstone && docker compose up --build                  # bitirme projesi
./scripts/kind-up.sh && helm install bookstore capstone/k8s/helm   # capstone Kubernetes'te
```

## Project Structure

```
spring-boot-edu/
  CLAUDE.md, SPEC.md, README.md (TR+EN), LICENSE
  pom.xml                     # aggregator (lesson + solution modülleri; exercise'ler -Pexercises profilinde)
  build-parent/pom.xml        # spring-boot-starter-parent 4.1.1, Java 27, enforcer, surefire/failsafe
  compose.yaml                # tüm servisler, profillerle gruplanmış
  .mvn/wrapper/, mvnw, mvnw.cmd
  .github/workflows/ci.yml    # build + test + doküman kontrolü
  docs/templates/             # ders/ödev şablonları (tr, en), pandoc şablonu, font ayarları
  docs/syllabus.tr.md, docs/syllabus.en.md (+ PDF)   # tüm müfredat
  scripts/                    # build-pdfs.sh, check-module.sh, new-module.sh
  modules/00-.../ … 24-.../
  capstone/                   # çok servisli proje: Dockerfile'lar, compose.yaml, k8s/ (Helm chart)
  tasks/plan.md, tasks/todo.md
```

## Code Style

Kurallar CLAUDE.md'de. Özet: constructor injection, Lombok yok, DTO'lar `record`, hatalar `ProblemDetail`, feature bazlı paketleme, JSpecify `@NullMarked`, modern API'ler (`RestClient`, `JdbcClient`, `MockMvcTester`).

## Testing Strategy

- **Unit** (`*Test`, Surefire): saf Java/Mockito, hızlı.
- **Slice** (`*Test`): `@WebMvcTest`, `@DataJpaTest`, `@DataMongoTest`, `@DataRedisTest`, `@DataElasticsearchTest`, `@JsonTest` …
- **Integration** (`*IT`, Failsafe): `@SpringBootTest` + Testcontainers + `@ServiceConnection`. Manuel başlatılmış container'a asla bağımlı olmaz.
- **Ödev testleri:** `exercise/` ve `solution/` aynı test dosyalarını içerir; `check-module.sh` farkı kontrol eder.
- **Doküman kontrolü:** TR/EN başlık sayısı ve kod bloğu sayısı eşleşmeli; PDF'ler MD'den yeni olmalı.
- Coverage hedefi zorunlu değil; kural: **her örneğin en az bir testi var**.

## Boundaries

- **Always:** Değiştirilen modülde `./mvnw verify` çalıştır; TR/EN'i aynı commit'te senkron tut; BOM sürümlerini kullan; her modül bağımsız çalışsın; dokümandaki kod derlenen kaynaktan gelsin.
- **Ask first:** SPEC'te olmayan bağımlılık; Java/Boot sürüm değişikliği; modül ekleme/silme/yeniden numaralama; ortak domain modelini değiştirme; CI değişikliği.
- **Never:** Secret commit etmek; Lombok/field injection; `exercise/` içine çözüm koymak; testi kapatıp yeşile çekmek; test edilmemiş kodu dokümana koymak; açıklama olmadan deprecated API kullanmak.

## Success Criteria

1. Temiz bir makinede (JDK 27 + Docker) `git clone` → `./mvnw verify` yeşil (exercise'ler hariç).
2. Her modül `spring-boot:run` ile tek komutla ayağa kalkar; gerekli altyapıyı Docker Compose desteği otomatik başlatır.
3. 25 modül + capstone'un her birinde: TR ve EN ders + ödev MD'si ve güncel PDF'leri mevcut.
4. Her modülde ≥ 5 çalıştırılabilir örnek, ≥ 3 ödev; tüm `solution/` testleri yeşil, tüm `exercise/` projeleri derlenir.
5. `./scripts/check-module.sh` tüm modüllerde geçer; CI (GitHub Actions) yeşil.
6. Capstone `docker compose up --build` ile tek komutla ayağa kalkar; PostgreSQL, MongoDB, Elasticsearch, Redis, Kafka ve Hazelcast'in hepsini gerçek bir akışta kullanır (sipariş ver → event → arama indeksi → cache) ve uçtan uca test edilir. Aynı sistem kind cluster'ında Helm ile de çalışır.
7. Lesson kodunda deprecated API uyarısı yok (`-Xlint:deprecation` ile kontrol).

## Kararlar (Open Questions — 2026-09-23'te kapatıldı)

1. groupId/paket: `com.springbootedu`.
2. Hedef kitle: Java bilen, Spring'e yeni/orta seviye. Süre hedefi verilmedi; müfredat dokümanı (C.8) önerilen haftalık akışı içerir.
3. `24-spring-ai` kapsamda; model Ollama (lokal). Testlerde gerçek model yerine mock/Testcontainers Ollama.
4. gRPC (`21`), Kubernetes (`22`), Spring Cloud (`23`) kapsama eklendi.
5. Lisans: kod MIT, doküman CC BY-SA 4.0.
6. Java 27 Docker base image (T0.2, 2026-09-23): `eclipse-temurin:27` henüz yok; **`amazoncorretto:27-alpine`** (JDK, jlink içerir) kullanılır. Runtime image'ları jlink ile küçültülür.
7. PDF: kapak sayfası, logo ve renk teması var (T0.5).
8. **GraalVM for JDK 27 yok** (en güncel: GraalVM CE 25.0.4). `19-native-performance` modülünün native-image kısmı **GraalVM 25 ile ve `release 25`** ile derlenir; JVM AOT cache/CDS kısmı Java 27'de kalır. GraalVM 27 çıkınca güncellenir.
9. Java 27 dil/API durumu (T0.2 probe): record/sealed/pattern matching, `ScopedValue`, stream gatherers, virtual threads, compact source files (`void main` + `IO`) **final**; `StructuredTaskScope` hâlâ **preview** → `00-setup-modern-java` içinde ayrı `preview` profilinde gösterilir.

10. **Hazelcast CP Subsystem Enterprise'a özel** (2026-09-23 spike): Hazelcast 5.5.0 Community'de `getCPSubsystem().getLock()` / `getAtomicLong()` → `UnsupportedOperationException: CP subsystem is a licensed feature`. Dağıtık kilit **`IMap.lock/tryLock`** (anahtar başına kilit) ile, atomik güncellemeler **`EntryProcessor`** ile öğretilir. `FencedLock`/CP yalnızca kavram olarak, "Enterprise özelliği" notuyla anlatılır. Modül 09 ve capstone bu karara göre yazılır.
11. **Modül 14'te ödevler test yazmaktır** (2026-09-24, kullanıcı kararı): `14-testing` için karar 8'in istisnası. `exercise/src/test` TODO'lu test iskeletlerini, `solution/src/test` tam testleri içerir; test edilen kod (`src/main`) iki modülde aynıdır. `check-module` bu modülde "testler aynı" kuralı yerine şunları doğrular: TODO'lar `exercise/src/test`'te, `src/main` iki tarafta aynı, çözümde TODO yok. Çözüm testlerinin yeterliliği bir mini mutasyon testiyle kanıtlanır: kasıtlı hatalı varyantlar çözüm testleri tarafından yakalanmalıdır.

## Open Questions

- (yok)
