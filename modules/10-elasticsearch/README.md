# 10 · Elasticsearch ile Arama / Search with Elasticsearch

🇹🇷 [Ders notları](docs/tr/ders.md) ([PDF](docs/tr/ders.pdf)) · [Ödevler](docs/tr/odevler.md) ([PDF](docs/tr/odevler.pdf))
🇬🇧 [Lesson notes](docs/en/lesson.md) ([PDF](docs/en/lesson.pdf)) · [Exercises](docs/en/exercises.md) ([PDF](docs/en/exercises.pdf))

## 🇹🇷 Bu modülde

- `@Document` eşlemesi, Türkçe analyzer, `keyword` ve `text` alanları
- `ElasticsearchRepository` ve türetilmiş sorgular
- `NativeQuery` ile bool sorgu: tam metin arama, filtreler, alan ağırlıkları, highlight
- Aggregation ile facet'ler, arama API'si
- PostgreSQL → Elasticsearch senkronizasyonu (`@TransactionalEventListener`), Testcontainers

## 🇬🇧 In this module

- `@Document` mapping, the Turkish analyzer, `keyword` vs `text` fields
- `ElasticsearchRepository` and derived queries
- Bool queries with `NativeQuery`: full-text search, filters, field boosts, highlighting
- Facets with aggregations, a search API
- PostgreSQL → Elasticsearch sync (`@TransactionalEventListener`), Testcontainers

## Çalıştırma / How to run

Ön koşul / Prerequisite: JDK 27, Docker — `export JAVA_HOME=$(/usr/libexec/java_home -v 27)`

```bash
# Ders kodunu çalıştır — gereken altyapı Docker Compose ile otomatik başlar
# Run the lesson — required infrastructure starts automatically via Docker Compose
./mvnw -pl modules/10-elasticsearch/lesson spring-boot:run

# Altyapı zaten çalışıyorsa (docker compose --profile ... up -d) / If infrastructure is already running
./mvnw -pl modules/10-elasticsearch/lesson spring-boot:run -Dspring-boot.run.arguments=--spring.docker.compose.enabled=false

# Testler / Tests (Testcontainers)
./mvnw -pl modules/10-elasticsearch/lesson verify

# Ödevler / Exercises (kırmızı başlar / start red)
./mvnw -Pexercises -pl modules/10-elasticsearch/exercise test
```

HTTP örnekleri / HTTP examples: [requests.http](requests.http)

## Yapı / Layout

| Klasör / Folder | İçerik / Content |
|---|---|
| `lesson/` | Çalışan örnekler + testler / Runnable examples + tests |
| `exercise/` | TODO'lu başlangıç kodu / Starter code with TODOs |
| `solution/` | Referans çözüm / Reference solution |
| `docs/` | Ders ve ödev dokümanları (MD + PDF) / Lesson & exercise docs |
