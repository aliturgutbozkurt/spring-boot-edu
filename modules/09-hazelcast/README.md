# 09 · Hazelcast ile Dağıtık Veri / Distributed Data with Hazelcast

🇹🇷 [Ders notları](docs/tr/ders.md) ([PDF](docs/tr/ders.pdf)) · [Ödevler](docs/tr/odevler.md) ([PDF](docs/tr/odevler.pdf))
🇬🇧 [Lesson notes](docs/en/lesson.md) ([PDF](docs/en/lesson.pdf)) · [Exercises](docs/en/exercises.md) ([PDF](docs/en/exercises.pdf))

## 🇹🇷 Bu modülde

- Embedded üye ve client-server topolojisi (Docker Compose, `client` profili)
- `IMap`: `set`, `putIfAbsent`, kayıt başına TTL, üyelerde çalışan sorgular
- Hazelcast ile Spring Cache (`@Cacheable`)
- `IMap.lock/tryLock` ile dağıtık kilit, kilitsiz `EntryProcessor`
- Near cache, iki üyeli cluster ve yedekler, Testcontainers

## 🇬🇧 In this module

- Embedded member and client-server topology (Docker Compose, `client` profile)
- `IMap`: `set`, `putIfAbsent`, TTL per entry, queries that run on the members
- Spring Cache (`@Cacheable`) backed by Hazelcast
- Distributed locking with `IMap.lock/tryLock`, lock-free `EntryProcessor`
- Near cache, a two-member cluster with backups, Testcontainers

## Çalıştırma / How to run

Ön koşul / Prerequisite: JDK 27, Docker — `export JAVA_HOME=$(/usr/libexec/java_home -v 27)`

```bash
# Ders kodunu çalıştır — Hazelcast uygulamanın içinde (embedded) çalışır, Docker gerekmez. Ctrl+C ile durdurun.
# Run the lesson — Hazelcast runs embedded in the application, no Docker needed. Stop it with Ctrl+C.
./mvnw -pl modules/09-hazelcast/lesson spring-boot:run

# Client-server: compose.yaml'daki Hazelcast üyesi otomatik başlar / the Hazelcast member from compose.yaml starts automatically
./mvnw -pl modules/09-hazelcast/lesson spring-boot:run -Dspring-boot.run.profiles=client

# Testler / Tests (Testcontainers)
./mvnw -pl modules/09-hazelcast/lesson verify

# Ödevler / Exercises (kırmızı başlar / start red)
./mvnw -Pexercises -pl modules/09-hazelcast/exercise test
```

HTTP örnekleri / HTTP examples: [requests.http](requests.http)

## Yapı / Layout

| Klasör / Folder | İçerik / Content |
|---|---|
| `lesson/` | Çalışan örnekler + testler / Runnable examples + tests |
| `exercise/` | TODO'lu başlangıç kodu / Starter code with TODOs |
| `solution/` | Referans çözüm / Reference solution |
| `docs/` | Ders ve ödev dokümanları (MD + PDF) / Lesson & exercise docs |
