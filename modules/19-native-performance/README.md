# 19 · Native Image ve Performans / Native Image and Performance

🇹🇷 [Ders notları](docs/tr/ders.md) ([PDF](docs/tr/ders.pdf)) · [Ödevler](docs/tr/odevler.md) ([PDF](docs/tr/odevler.pdf))
🇬🇧 [Lesson notes](docs/en/lesson.md) ([PDF](docs/en/lesson.pdf)) · [Exercises](docs/en/exercises.md) ([PDF](docs/en/exercises.pdf))

## 🇹🇷 Bu modülde

- Spring AOT işleme (`process-aot`) ve üretilen kod; Spring Data JDBC AOT repositories
- `RuntimeHints` ile reflection ve kaynak ipuçları, `RuntimeHintsPredicates` ile testleri
- GraalVM 25 native image (Paketo buildpacks ile Docker içinde, yerelde GraalVM gerekmez)
- JDK 27'de AOT cache (Project Leyden) ile başlangıç iyileştirme
- Başlangıç süresi ve bellek karşılaştırma script'i: `compare-startup.sh`

## 🇬🇧 In this module

- Spring AOT processing (`process-aot`) and the generated code; Spring Data JDBC AOT repositories
- Reflection and resource hints with `RuntimeHints`, tested with `RuntimeHintsPredicates`
- A GraalVM 25 native image (built inside Docker with Paketo buildpacks, no local GraalVM needed)
- Faster startup on JDK 27 with the AOT cache (Project Leyden)
- A startup time and memory comparison script: `compare-startup.sh`

## Çalıştırma / How to run

Ön koşul / Prerequisite: JDK 27, Docker — `export JAVA_HOME=$(/usr/libexec/java_home -v 27)`

```bash
# Ders kodunu çalıştır — gereken altyapı Docker Compose ile otomatik başlar
# Run the lesson — required infrastructure starts automatically via Docker Compose
./mvnw -pl modules/19-native-performance/lesson spring-boot:run

# Altyapı zaten çalışıyorsa (docker compose --profile ... up -d) / If infrastructure is already running
./mvnw -pl modules/19-native-performance/lesson spring-boot:run -Dspring-boot.run.arguments=--spring.docker.compose.enabled=false

# Testler / Tests (Testcontainers)
./mvnw -pl modules/19-native-performance/lesson verify

# Ödevler / Exercises (kırmızı başlar / start red)
./mvnw -Pexercises -pl modules/19-native-performance/exercise test
```

```bash
# Native image (Docker içinde GraalVM 25, birkaç dakika sürer / GraalVM 25 inside Docker, takes a few minutes)
./mvnw -Pnative -pl modules/19-native-performance/lesson spring-boot:build-image
docker compose --profile postgres up -d
docker run --rm -p 8080:8080 springbootedu/native-performance:1.0.0-SNAPSHOT \
  --spring.datasource.url=jdbc:postgresql://host.docker.internal:5432/bookstore \
  --spring.datasource.username=bookstore --spring.datasource.password=bookstore

# Başlangıç karşılaştırması / Startup comparison (JVM, AOT, AOT cache, native)
modules/19-native-performance/compare-startup.sh
```

HTTP örnekleri / HTTP examples: [requests.http](requests.http)

## Yapı / Layout

| Klasör / Folder | İçerik / Content |
|---|---|
| `lesson/` | Çalışan örnekler + testler / Runnable examples + tests |
| `exercise/` | TODO'lu başlangıç kodu / Starter code with TODOs |
| `solution/` | Referans çözüm / Reference solution |
| `docs/` | Ders ve ödev dokümanları (MD + PDF) / Lesson & exercise docs |
