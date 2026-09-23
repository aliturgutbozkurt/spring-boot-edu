# 05 · Spring JDBC ve PostgreSQL / Spring JDBC and PostgreSQL

🇹🇷 [Ders notları](docs/tr/ders.md) ([PDF](docs/tr/ders.pdf)) · [Ödevler](docs/tr/odevler.md) ([PDF](docs/tr/odevler.pdf))
🇬🇧 [Lesson notes](docs/en/lesson.md) ([PDF](docs/en/lesson.pdf)) · [Exercises](docs/en/exercises.md) ([PDF](docs/en/exercises.pdf))

## 🇹🇷 Bu modülde

- Docker Compose desteğiyle otomatik PostgreSQL, HikariCP
- Flyway migration'ları
- `JdbcClient` (record eşleme, join, üretilen anahtar), batch
- Spring Data JDBC aggregate'leri
- `@Transactional`, `REQUIRES_NEW`, `@TransactionalEventListener`
- Testcontainers + `@ServiceConnection`, `@JdbcTest`, `@DataJdbcTest`

## 🇬🇧 In this module

- Automatic PostgreSQL with Docker Compose support, HikariCP
- Flyway migrations
- `JdbcClient` (record mapping, joins, generated keys), batches
- Spring Data JDBC aggregates
- `@Transactional`, `REQUIRES_NEW`, `@TransactionalEventListener`
- Testcontainers + `@ServiceConnection`, `@JdbcTest`, `@DataJdbcTest`

## Çalıştırma / How to run

Ön koşul / Prerequisite: JDK 27 ve **Docker** — `export JAVA_HOME=$(/usr/libexec/java_home -v 27)`

```bash
# PostgreSQL otomatik başlar / PostgreSQL starts automatically
./mvnw -pl modules/05-data-jdbc-postgres/lesson -am spring-boot:run

# Zaten çalışan bir compose servisi varsa Boot onu kullanır, yenisini başlatmaz.
# If the compose service is already running, Boot connects to it instead of starting another one.

# Temiz veritabanı / Clean database
docker compose --profile postgres down -v

# Testler / Tests (Testcontainers)
./mvnw -pl modules/05-data-jdbc-postgres/lesson -am verify

# Ödevler / Exercises (kırmızı başlar / start red)
./mvnw -Pexercises -pl modules/05-data-jdbc-postgres/exercise -am test
```

## Yapı / Layout

| Klasör / Folder | İçerik / Content |
|---|---|
| `lesson/` | Çalışan örnekler + testler / Runnable examples + tests |
| `lesson/src/main/resources/db/migration/` | Flyway migration'ları / Flyway migrations |
| `exercise/` | TODO'lu başlangıç kodu / Starter code with TODOs |
| `solution/` | Referans çözüm / Reference solution |
| `docs/` | Ders ve ödev dokümanları (MD + PDF) / Lesson & exercise docs |
