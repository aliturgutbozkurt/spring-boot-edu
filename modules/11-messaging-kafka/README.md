# 11 · Kafka ile Mesajlaşma / Messaging with Kafka

🇹🇷 [Ders notları](docs/tr/ders.md) ([PDF](docs/tr/ders.pdf)) · [Ödevler](docs/tr/odevler.md) ([PDF](docs/tr/odevler.pdf))
🇬🇧 [Lesson notes](docs/en/lesson.md) ([PDF](docs/en/lesson.pdf)) · [Exercises](docs/en/exercises.md) ([PDF](docs/en/exercises.pdf))

## 🇹🇷 Bu modülde

- `KafkaTemplate` ile JSON event üretmek, `@KafkaListener` ve consumer group'lar
- `DefaultErrorHandler`: yerinde retry ve dead letter topic (DLT), zehirli mesajlar
- `@RetryableTopic` ile bloklamayan retry'lar
- Kafka transaction'ları ve `read_committed`
- PostgreSQL ile transactional outbox
- Kafka Streams ile sayaç, `TopologyTestDriver`, Testcontainers

## 🇬🇧 In this module

- Producing JSON events with `KafkaTemplate`, `@KafkaListener` and consumer groups
- `DefaultErrorHandler`: in-place retries and the dead letter topic (DLT), poison pills
- Non-blocking retries with `@RetryableTopic`
- Kafka transactions and `read_committed`
- The transactional outbox with PostgreSQL
- A Kafka Streams counter, `TopologyTestDriver`, Testcontainers

## Çalıştırma / How to run

Ön koşul / Prerequisite: JDK 27, Docker — `export JAVA_HOME=$(/usr/libexec/java_home -v 27)`

```bash
# Ders kodunu çalıştır — gereken altyapı Docker Compose ile otomatik başlar
# Run the lesson — required infrastructure starts automatically via Docker Compose
./mvnw -pl modules/11-messaging-kafka/lesson spring-boot:run

# Altyapı zaten çalışıyorsa (docker compose --profile ... up -d) / If infrastructure is already running
./mvnw -pl modules/11-messaging-kafka/lesson spring-boot:run -Dspring-boot.run.arguments=--spring.docker.compose.enabled=false

# Testler / Tests (Testcontainers)
./mvnw -pl modules/11-messaging-kafka/lesson verify

# Ödevler / Exercises (kırmızı başlar / start red)
./mvnw -Pexercises -pl modules/11-messaging-kafka/exercise test
```

Kafka komut satırı örnekleri / Kafka CLI examples: [requests.http](requests.http)

## Yapı / Layout

| Klasör / Folder | İçerik / Content |
|---|---|
| `lesson/` | Çalışan örnekler + testler / Runnable examples + tests |
| `exercise/` | TODO'lu başlangıç kodu / Starter code with TODOs |
| `solution/` | Referans çözüm / Reference solution |
| `docs/` | Ders ve ödev dokümanları (MD + PDF) / Lesson & exercise docs |
