# 06 · Spring Data JPA ve Hibernate / Spring Data JPA and Hibernate

🇹🇷 [Ders notları](docs/tr/ders.md) ([PDF](docs/tr/ders.pdf)) · [Ödevler](docs/tr/odevler.md) ([PDF](docs/tr/odevler.pdf))
🇬🇧 [Lesson notes](docs/en/lesson.md) ([PDF](docs/en/lesson.pdf)) · [Exercises](docs/en/exercises.md) ([PDF](docs/en/exercises.pdf))

## 🇹🇷 Bu modülde

- Entity eşleme, 1–N ve N–N ilişkiler, persistence context, dirty checking
- Türetilmiş sorgular, sayfalama ve sıralama
- N+1 problemi: ölçmek, `@EntityGraph` ve `join fetch` ile düzeltmek
- Interface ve record projection'ları, `Specification` ile dinamik arama
- Auditing, `@Version` ile iyimser ve `FOR UPDATE` ile kötümser kilitleme

## 🇬🇧 In this module

- Entity mapping, 1–N and N–N relationships, persistence context, dirty checking
- Derived queries, paging and sorting
- The N+1 problem: measuring it, fixing it with `@EntityGraph` and `join fetch`
- Interface and record projections, dynamic search with `Specification`
- Auditing, optimistic locking with `@Version`, pessimistic locking with `FOR UPDATE`

## Çalıştırma / How to run

Ön koşul / Prerequisite: JDK 27 ve **Docker** — `export JAVA_HOME=$(/usr/libexec/java_home -v 27)`

```bash
# PostgreSQL otomatik başlar / PostgreSQL starts automatically
./mvnw -pl modules/06-data-jpa-postgres/lesson -am spring-boot:run

# Temiz veritabanı / Clean database
docker compose --profile postgres down -v

# Testler / Tests (Testcontainers)
./mvnw -pl modules/06-data-jpa-postgres/lesson -am verify

# Ödevler / Exercises (kırmızı başlar / start red)
./mvnw -Pexercises -pl modules/06-data-jpa-postgres/exercise -am test
```

## Yapı / Layout

| Klasör / Folder | İçerik / Content |
|---|---|
| `lesson/` | Çalışan örnekler + testler / Runnable examples + tests |
| `exercise/` | TODO'lu başlangıç kodu / Starter code with TODOs |
| `solution/` | Referans çözüm / Reference solution |
| `docs/` | Ders ve ödev dokümanları (MD + PDF) / Lesson & exercise docs |
