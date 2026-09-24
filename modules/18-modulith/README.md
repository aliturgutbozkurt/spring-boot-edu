# 18 · Spring Modulith / Spring Modulith

🇹🇷 [Ders notları](docs/tr/ders.md) ([PDF](docs/tr/ders.pdf)) · [Ödevler](docs/tr/odevler.md) ([PDF](docs/tr/odevler.pdf))
🇬🇧 [Lesson notes](docs/en/lesson.md) ([PDF](docs/en/lesson.pdf)) · [Exercises](docs/en/exercises.md) ([PDF](docs/en/exercises.pdf))

## 🇹🇷 Bu modülde

- Paketlerle modül sınırları, `ApplicationModules.verify()` ve `@ApplicationModule(allowedDependencies = ...)`
- Application event'leri ve `@ApplicationModuleListener` ile modüller arası gevşek bağlı iletişim
- JDBC event publication registry: başarısız listener'lar, `IncompleteEventPublications` ile yeniden gönderim
- `@Externalized` ile event'leri Kafka'ya taşıma
- `@ApplicationModuleTest` ve `Scenario` ile modül testleri, `Documenter` ile C4/PlantUML dokümantasyonu

## 🇬🇧 In this module

- Module boundaries with packages, `ApplicationModules.verify()` and `@ApplicationModule(allowedDependencies = ...)`
- Loosely coupled communication between modules with application events and `@ApplicationModuleListener`
- The JDBC event publication registry: failed listeners, resubmission with `IncompleteEventPublications`
- Event externalization to Kafka with `@Externalized`
- Module tests with `@ApplicationModuleTest` and `Scenario`, C4/PlantUML documentation with `Documenter`

## Çalıştırma / How to run

Ön koşul / Prerequisite: JDK 27, Docker — `export JAVA_HOME=$(/usr/libexec/java_home -v 27)`

```bash
# Ders kodunu çalıştır — gereken altyapı Docker Compose ile otomatik başlar
# Run the lesson — required infrastructure starts automatically via Docker Compose
./mvnw -pl modules/18-modulith/lesson spring-boot:run

# Altyapı zaten çalışıyorsa (docker compose --profile ... up -d) / If infrastructure is already running
./mvnw -pl modules/18-modulith/lesson spring-boot:run -Dspring-boot.run.arguments=--spring.docker.compose.enabled=false

# Testler / Tests (Testcontainers)
./mvnw -pl modules/18-modulith/lesson verify

# Ödevler / Exercises (kırmızı başlar / start red)
./mvnw -Pexercises -pl modules/18-modulith/exercise test
```

## Yapı / Layout

| Klasör / Folder | İçerik / Content |
|---|---|
| `lesson/` | Çalışan örnekler + testler / Runnable examples + tests |
| `exercise/` | TODO'lu başlangıç kodu / Starter code with TODOs |
| `solution/` | Referans çözüm / Reference solution |
| `docs/` | Ders ve ödev dokümanları (MD + PDF) / Lesson & exercise docs |
