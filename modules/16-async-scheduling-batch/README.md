# 16 · Async, Zamanlama ve Spring Batch / Async, Scheduling and Spring Batch

🇹🇷 [Ders notları](docs/tr/ders.md) ([PDF](docs/tr/ders.pdf)) · [Ödevler](docs/tr/odevler.md) ([PDF](docs/tr/odevler.pdf))
🇬🇧 [Lesson notes](docs/en/lesson.md) ([PDF](docs/en/lesson.pdf)) · [Exercises](docs/en/exercises.md) ([PDF](docs/en/exercises.pdf))

## 🇹🇷 Bu modülde

- `@Async` ve `CompletableFuture`, virtual thread'li executor
- `@Scheduled` (fixedDelay, cron, saat dilimi) ve birden çok örnekte ShedLock
- Spring Batch 6: CSV → PostgreSQL chunk job'ı, skip/retry, job parametreleri, yeniden başlatma

## 🇬🇧 In this module

- `@Async` and `CompletableFuture`, an executor with virtual threads
- `@Scheduled` (fixedDelay, cron, time zone) and ShedLock for several instances
- Spring Batch 6: a CSV → PostgreSQL chunk job, skip/retry, job parameters, restart

## Çalıştırma / How to run

Ön koşul / Prerequisite: JDK 27, Docker — `export JAVA_HOME=$(/usr/libexec/java_home -v 27)`

```bash
# Ders kodunu çalıştır — gereken altyapı Docker Compose ile otomatik başlar
# Run the lesson — required infrastructure starts automatically via Docker Compose
./mvnw -pl modules/16-async-scheduling-batch/lesson spring-boot:run

# Altyapı zaten çalışıyorsa (docker compose --profile ... up -d) / If infrastructure is already running
./mvnw -pl modules/16-async-scheduling-batch/lesson spring-boot:run -Dspring-boot.run.arguments=--spring.docker.compose.enabled=false

# Testler / Tests (Testcontainers)
./mvnw -pl modules/16-async-scheduling-batch/lesson verify

# Ödevler / Exercises (kırmızı başlar / start red)
./mvnw -Pexercises -pl modules/16-async-scheduling-batch/exercise test
```

SQL örnekleri / SQL examples: [requests.http](requests.http)

## Yapı / Layout

| Klasör / Folder | İçerik / Content |
|---|---|
| `lesson/` | Çalışan örnekler + testler / Runnable examples + tests |
| `exercise/` | TODO'lu başlangıç kodu / Starter code with TODOs |
| `solution/` | Referans çözüm / Reference solution |
| `docs/` | Ders ve ödev dokümanları (MD + PDF) / Lesson & exercise docs |
