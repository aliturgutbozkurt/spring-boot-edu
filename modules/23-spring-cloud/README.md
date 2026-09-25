# 23 · Spring Cloud / Spring Cloud

🇹🇷 [Ders notları](docs/tr/ders.md) ([PDF](docs/tr/ders.pdf)) · [Ödevler](docs/tr/odevler.md) ([PDF](docs/tr/odevler.pdf))
🇬🇧 [Lesson notes](docs/en/lesson.md) ([PDF](docs/en/lesson.pdf)) · [Exercises](docs/en/exercises.md) ([PDF](docs/en/exercises.pdf))

## 🇹🇷 Bu modülde

- `services/gateway`: Spring Cloud Gateway — route'lar, filtreler, Redis ile rate limit
- `services/config-server`: Config Server (Git backend; compose'da `native`) ve `config-repo/`
- `lesson/` (sipariş servisi): OpenFeign ile HTTP interface karşılaştırması, Spring Cloud LoadBalancer, Resilience4j circuit breaker + fallback, `@RefreshScope`
- `services/catalog-service`: iki örnekle çalışan katalog; hata/gecikme simülasyonu
- Spring Cloud Kubernetes: ConfigMap okuma, pod keşfi ve yeniden yükleme (`k8s/`, kind)

## 🇬🇧 In this module

- `services/gateway`: Spring Cloud Gateway — routes, filters, rate limiting with Redis
- `services/config-server`: Config Server (Git backend; `native` in compose) and `config-repo/`
- `lesson/` (order service): OpenFeign compared with an HTTP interface, Spring Cloud LoadBalancer, Resilience4j circuit breaker + fallback, `@RefreshScope`
- `services/catalog-service`: the catalog, running as two instances; failure/latency simulation
- Spring Cloud Kubernetes: reading ConfigMaps, pod discovery and reload (`k8s/`, kind)

## Çalıştırma / How to run

Ön koşul / Prerequisite: JDK 27, Docker — `export JAVA_HOME=$(/usr/libexec/java_home -v 27)`

```bash
# Ders kodunu çalıştır — gereken altyapı Docker Compose ile otomatik başlar
# Run the lesson — required infrastructure starts automatically via Docker Compose
./mvnw -pl modules/23-spring-cloud/lesson spring-boot:run

# Altyapı zaten çalışıyorsa (docker compose --profile ... up -d) / If infrastructure is already running
./mvnw -pl modules/23-spring-cloud/lesson spring-boot:run -Dspring-boot.run.arguments=--spring.docker.compose.enabled=false

# Testler / Tests (Testcontainers)
./mvnw -pl modules/23-spring-cloud/lesson verify

# Ödevler / Exercises (kırmızı başlar / start red)
./mvnw -Pexercises -pl modules/23-spring-cloud/exercise test
```

```bash
# Tüm sistem compose ile / The whole system with compose (gateway: http://localhost:9000)
modules/23-spring-cloud/up.sh
docker compose -f modules/23-spring-cloud/compose.yaml down

# Kubernetes (kind) + Spring Cloud Kubernetes
scripts/kind-up.sh
modules/23-spring-cloud/up.sh && modules/23-spring-cloud/deploy-k8s.sh
```

HTTP örnekleri / HTTP examples: [requests.http](requests.http)

## Yapı / Layout

| Klasör / Folder | İçerik / Content |
|---|---|
| `lesson/` | Çalışan örnekler + testler / Runnable examples + tests |
| `exercise/` | TODO'lu başlangıç kodu / Starter code with TODOs |
| `solution/` | Referans çözüm / Reference solution |
| `docs/` | Ders ve ödev dokümanları (MD + PDF) / Lesson & exercise docs |
