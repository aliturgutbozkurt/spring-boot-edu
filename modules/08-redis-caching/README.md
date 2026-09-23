# 08 · Redis ve Önbellekleme / Redis and Caching

🇹🇷 [Ders notları](docs/tr/ders.md) ([PDF](docs/tr/ders.pdf)) · [Ödevler](docs/tr/odevler.md) ([PDF](docs/tr/odevler.pdf))
🇬🇧 [Lesson notes](docs/en/lesson.md) ([PDF](docs/en/lesson.pdf)) · [Exercises](docs/en/exercises.md) ([PDF](docs/en/exercises.pdf))

## 🇹🇷 Bu modülde

- `@Cacheable` / `@CachePut` / `@CacheEvict`: cache-aside ve write-through
- Redis cache manager: önbellek başına TTL, JSON değerler, anında yazma
- Veri yapıları: sorted set (en çok satanlar), list (son bakılanlar), atomik sayaçlar
- Pub/sub ile mesajlaşma, Spring Session ile Redis'te HTTP oturumu
- `@DataRedisTest` ve Testcontainers

## 🇬🇧 In this module

- `@Cacheable` / `@CachePut` / `@CacheEvict`: cache-aside and write-through
- The Redis cache manager: TTL per cache, JSON values, immediate writes
- Data structures: sorted set (best sellers), list (recently viewed), atomic counters
- Pub/sub messaging, the HTTP session in Redis with Spring Session
- `@DataRedisTest` and Testcontainers

## Çalıştırma / How to run

Ön koşul / Prerequisite: JDK 27, Docker — `export JAVA_HOME=$(/usr/libexec/java_home -v 27)`

```bash
# Ders kodunu çalıştır — gereken altyapı Docker Compose ile otomatik başlar
# Run the lesson — required infrastructure starts automatically via Docker Compose
./mvnw -pl modules/08-redis-caching/lesson spring-boot:run

# Altyapı zaten çalışıyorsa (docker compose --profile ... up -d) / If infrastructure is already running
./mvnw -pl modules/08-redis-caching/lesson spring-boot:run -Dspring-boot.run.arguments=--spring.docker.compose.enabled=false

# Testler / Tests (Testcontainers)
./mvnw -pl modules/08-redis-caching/lesson verify

# Ödevler / Exercises (kırmızı başlar / start red)
./mvnw -Pexercises -pl modules/08-redis-caching/exercise test
```

HTTP örnekleri / HTTP examples: [requests.http](requests.http)

## Yapı / Layout

| Klasör / Folder | İçerik / Content |
|---|---|
| `lesson/` | Çalışan örnekler + testler / Runnable examples + tests |
| `exercise/` | TODO'lu başlangıç kodu / Starter code with TODOs |
| `solution/` | Referans çözüm / Reference solution |
| `docs/` | Ders ve ödev dokümanları (MD + PDF) / Lesson & exercise docs |
