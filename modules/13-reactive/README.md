# 13 · Reactive Programlama ve WebFlux / Reactive Programming and WebFlux

🇹🇷 [Ders notları](docs/tr/ders.md) ([PDF](docs/tr/ders.pdf)) · [Ödevler](docs/tr/odevler.md) ([PDF](docs/tr/odevler.pdf))
🇬🇧 [Lesson notes](docs/en/lesson.md) ([PDF](docs/en/lesson.pdf)) · [Exercises](docs/en/exercises.md) ([PDF](docs/en/exercises.pdf))

## 🇹🇷 Bu modülde

- Reactor: `Mono`, `Flux`, operatörler, hata yönetimi, backpressure, `StepVerifier`
- WebFlux: functional endpoint'ler ve annotation'lı controller'lar
- R2DBC ile PostgreSQL, reactive MongoDB, iki kaynağı `Mono.zip` ile birleştirmek
- Server-Sent Events ile canlı sipariş akışı
- Reactive mi, virtual thread mi?

## 🇬🇧 In this module

- Reactor: `Mono`, `Flux`, operators, error handling, backpressure, `StepVerifier`
- WebFlux: functional endpoints and annotated controllers
- PostgreSQL with R2DBC, reactive MongoDB, combining two sources with `Mono.zip`
- A live order feed with Server-Sent Events
- Reactive or virtual threads?

## Çalıştırma / How to run

Ön koşul / Prerequisite: JDK 27, Docker — `export JAVA_HOME=$(/usr/libexec/java_home -v 27)`

```bash
# Ders kodunu çalıştır — gereken altyapı Docker Compose ile otomatik başlar
# Run the lesson — required infrastructure starts automatically via Docker Compose
./mvnw -pl modules/13-reactive/lesson spring-boot:run

# Altyapı zaten çalışıyorsa (docker compose --profile ... up -d) / If infrastructure is already running
./mvnw -pl modules/13-reactive/lesson spring-boot:run -Dspring-boot.run.arguments=--spring.docker.compose.enabled=false

# Testler / Tests (Testcontainers)
./mvnw -pl modules/13-reactive/lesson verify

# Ödevler / Exercises (kırmızı başlar / start red)
./mvnw -Pexercises -pl modules/13-reactive/exercise test
```

HTTP örnekleri / HTTP examples: [requests.http](requests.http)

## Yapı / Layout

| Klasör / Folder | İçerik / Content |
|---|---|
| `lesson/` | Çalışan örnekler + testler / Runnable examples + tests |
| `exercise/` | TODO'lu başlangıç kodu / Starter code with TODOs |
| `solution/` | Referans çözüm / Reference solution |
| `docs/` | Ders ve ödev dokümanları (MD + PDF) / Lesson & exercise docs |
