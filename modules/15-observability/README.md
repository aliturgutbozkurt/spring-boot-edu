# 15 · Gözlemlenebilirlik: Metrikler, Trace'ler, Loglar / Observability: Metrics, Traces, Logs

🇹🇷 [Ders notları](docs/tr/ders.md) ([PDF](docs/tr/ders.pdf)) · [Ödevler](docs/tr/odevler.md) ([PDF](docs/tr/odevler.pdf))
🇬🇧 [Lesson notes](docs/en/lesson.md) ([PDF](docs/en/lesson.pdf)) · [Exercises](docs/en/exercises.md) ([PDF](docs/en/exercises.pdf))

## 🇹🇷 Bu modülde

- Actuator: health (özel `HealthIndicator`, liveness/readiness), info, metrics — ve neyin açılmaması gerektiği
- Micrometer: counter, timer, gauge; `@Observed`
- OpenTelemetry ile tracing, servisler arası trace, log korelasyonu (ECS JSON loglar)
- OTLP ile Grafana LGTM'ye metrik ve trace; hazır Grafana dashboard'u

## 🇬🇧 In this module

- Actuator: health (custom `HealthIndicator`, liveness/readiness), info, metrics — and what not to expose
- Micrometer: counter, timer, gauge; `@Observed`
- Tracing with OpenTelemetry, traces across services, log correlation (ECS JSON logs)
- Metrics and traces to Grafana LGTM over OTLP; a ready-made Grafana dashboard

## Çalıştırma / How to run

Ön koşul / Prerequisite: JDK 27, Docker — `export JAVA_HOME=$(/usr/libexec/java_home -v 27)`

```bash
# Ders kodunu çalıştır — gereken altyapı Docker Compose ile otomatik başlar
# Run the lesson — required infrastructure starts automatically via Docker Compose
./mvnw -pl modules/15-observability/lesson spring-boot:run

# Altyapı zaten çalışıyorsa (docker compose --profile ... up -d) / If infrastructure is already running
./mvnw -pl modules/15-observability/lesson spring-boot:run -Dspring-boot.run.arguments=--spring.docker.compose.enabled=false

# Testler / Tests (Testcontainers)
./mvnw -pl modules/15-observability/lesson verify

# Ödevler / Exercises (kırmızı başlar / start red)
./mvnw -Pexercises -pl modules/15-observability/exercise test
```

HTTP örnekleri / HTTP examples: [requests.http](requests.http)

Grafana: http://localhost:3000 — dashboard'u yükleyin / import the dashboard:

```bash
curl -X POST -H 'Content-Type: application/json' \
     -d @modules/15-observability/grafana/bookstore-orders-dashboard.json http://localhost:3000/api/dashboards/db
```

## Yapı / Layout

| Klasör / Folder | İçerik / Content |
|---|---|
| `lesson/` | Çalışan örnekler + testler / Runnable examples + tests |
| `exercise/` | TODO'lu başlangıç kodu / Starter code with TODOs |
| `solution/` | Referans çözüm / Reference solution |
| `docs/` | Ders ve ödev dokümanları (MD + PDF) / Lesson & exercise docs |
