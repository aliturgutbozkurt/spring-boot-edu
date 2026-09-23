# 07 · Spring Data MongoDB

🇹🇷 [Ders notları](docs/tr/ders.md) ([PDF](docs/tr/ders.pdf)) · [Ödevler](docs/tr/odevler.md) ([PDF](docs/tr/odevler.pdf))
🇬🇧 [Lesson notes](docs/en/lesson.md) ([PDF](docs/en/lesson.pdf)) · [Exercises](docs/en/exercises.md) ([PDF](docs/en/exercises.pdf))

## 🇹🇷 Bu modülde

- Belge modelleme: gömme ve referans (`@DocumentReference`), esnek alanlar
- `MongoRepository` türetilmiş ve JSON sorguları
- `MongoTemplate`: dinamik `Criteria`, atomik `$inc` / `$push`
- Aggregation pipeline, tekil ve metin index'leri
- Replica set üzerinde çok belgeli transaction'lar, Testcontainers

## 🇬🇧 In this module

- Document modelling: embedding vs referencing (`@DocumentReference`), flexible fields
- `MongoRepository` derived and JSON queries
- `MongoTemplate`: dynamic `Criteria`, atomic `$inc` / `$push`
- The aggregation pipeline, unique and text indexes
- Multi-document transactions on a replica set, Testcontainers

## Çalıştırma / How to run

Ön koşul / Prerequisite: JDK 27 ve **Docker** — `export JAVA_HOME=$(/usr/libexec/java_home -v 27)`

```bash
# MongoDB (replica set) otomatik başlar / MongoDB (replica set) starts automatically
./mvnw -pl modules/07-data-mongodb/lesson -am spring-boot:run

# Veriye bak / Look at the data
docker compose exec mongo mongosh bookstore --eval 'db.books.findOne()'

# Temiz veritabanı / Clean database
docker compose --profile mongo down -v

# Testler / Tests (Testcontainers)
./mvnw -pl modules/07-data-mongodb/lesson -am verify

# Ödevler / Exercises (kırmızı başlar / start red)
./mvnw -Pexercises -pl modules/07-data-mongodb/exercise -am test
```

## Yapı / Layout

| Klasör / Folder | İçerik / Content |
|---|---|
| `lesson/` | Çalışan örnekler + testler / Runnable examples + tests |
| `exercise/` | TODO'lu başlangıç kodu / Starter code with TODOs |
| `solution/` | Referans çözüm / Reference solution |
| `docs/` | Ders ve ödev dokümanları (MD + PDF) / Lesson & exercise docs |
