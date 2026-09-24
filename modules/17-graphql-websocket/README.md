# 17 · GraphQL ve WebSocket / GraphQL and WebSocket

🇹🇷 [Ders notları](docs/tr/ders.md) ([PDF](docs/tr/ders.pdf)) · [Ödevler](docs/tr/odevler.md) ([PDF](docs/tr/odevler.pdf))
🇬🇧 [Lesson notes](docs/en/lesson.md) ([PDF](docs/en/lesson.pdf)) · [Exercises](docs/en/exercises.md) ([PDF](docs/en/exercises.pdf))

## 🇹🇷 Bu modülde

- Schema-first Spring for GraphQL: `@QueryMapping`, `@MutationMapping`, `@SchemaMapping`, hatalar için `@GraphQlExceptionHandler`
- N+1 problemi ve `@BatchMapping` (DataLoader) ile çözümü
- WebSocket üzerinden subscription, `GraphQlTester` ile testler, GraphiQL (`/graphiql`)
- WebSocket/STOMP ile canlı fiyat bildirimi (`/topic/prices`, tarayıcı istemcisi: `/prices.html`)

## 🇬🇧 In this module

- Schema-first Spring for GraphQL: `@QueryMapping`, `@MutationMapping`, `@SchemaMapping`, `@GraphQlExceptionHandler` for errors
- The N+1 problem and its fix with `@BatchMapping` (DataLoader)
- Subscriptions over WebSocket, tests with `GraphQlTester`, GraphiQL (`/graphiql`)
- Live price notifications with WebSocket/STOMP (`/topic/prices`, browser client: `/prices.html`)

## Çalıştırma / How to run

Ön koşul / Prerequisite: JDK 27, Docker — `export JAVA_HOME=$(/usr/libexec/java_home -v 27)`

```bash
# Ders kodunu çalıştır — gereken altyapı Docker Compose ile otomatik başlar
# Run the lesson — required infrastructure starts automatically via Docker Compose
./mvnw -pl modules/17-graphql-websocket/lesson spring-boot:run

# Altyapı zaten çalışıyorsa (docker compose --profile ... up -d) / If infrastructure is already running
./mvnw -pl modules/17-graphql-websocket/lesson spring-boot:run -Dspring-boot.run.arguments=--spring.docker.compose.enabled=false

# Testler / Tests (Testcontainers)
./mvnw -pl modules/17-graphql-websocket/lesson verify

# Ödevler / Exercises (kırmızı başlar / start red)
./mvnw -Pexercises -pl modules/17-graphql-websocket/exercise test
```

HTTP örnekleri / HTTP examples: [requests.http](requests.http)

## Yapı / Layout

| Klasör / Folder | İçerik / Content |
|---|---|
| `lesson/` | Çalışan örnekler + testler / Runnable examples + tests |
| `exercise/` | TODO'lu başlangıç kodu / Starter code with TODOs |
| `solution/` | Referans çözüm / Reference solution |
| `docs/` | Ders ve ödev dokümanları (MD + PDF) / Lesson & exercise docs |
