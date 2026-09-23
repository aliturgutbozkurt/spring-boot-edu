# 04 · HTTP İstemcileri ve Dayanıklılık / HTTP Clients and Resilience

🇹🇷 [Ders notları](docs/tr/ders.md) ([PDF](docs/tr/ders.pdf)) · [Ödevler](docs/tr/odevler.md) ([PDF](docs/tr/odevler.pdf))
🇬🇧 [Lesson notes](docs/en/lesson.md) ([PDF](docs/en/lesson.pdf)) · [Exercises](docs/en/exercises.md) ([PDF](docs/en/exercises.pdf))

## 🇹🇷 Bu modülde

- `RestClient`, hata eşleme (`onStatus`), `RestClientCustomizer`
- HTTP interface istemcileri ve `@ImportHttpServices` (Spring Framework 7)
- `WebClient` karşılaştırması
- `@Retryable`, `@ConcurrencyLimit` (Spring Framework 7, ek kütüphane yok)
- Zaman aşımları, WireMock ile testler

## 🇬🇧 In this module

- `RestClient`, error mapping (`onStatus`), `RestClientCustomizer`
- HTTP interface clients and `@ImportHttpServices` (Spring Framework 7)
- A `WebClient` comparison
- `@Retryable`, `@ConcurrencyLimit` (Spring Framework 7, no extra library)
- Timeouts, testing with WireMock

## Çalıştırma / How to run

Ön koşul / Prerequisite: JDK 27 — `export JAVA_HOME=$(/usr/libexec/java_home -v 27)`. Bu modül Docker gerektirmez; "uzak" servis uygulamanın içinde çalışır. / No Docker needed; the "remote" service runs inside the app.

```bash
# Tüm örnekler / Every example (http://localhost:8080)
./mvnw -pl modules/04-http-clients-resilience/lesson -am spring-boot:run

# Testler / Tests (WireMock)
./mvnw -pl modules/04-http-clients-resilience/lesson -am verify

# Ödevler / Exercises (kırmızı başlar / start red)
./mvnw -Pexercises -pl modules/04-http-clients-resilience/exercise -am test
```

HTTP örnekleri / HTTP examples: [requests.http](requests.http)

## Yapı / Layout

| Klasör / Folder | İçerik / Content |
|---|---|
| `lesson/` | Çalışan örnekler + testler / Runnable examples + tests |
| `exercise/` | TODO'lu başlangıç kodu / Starter code with TODOs |
| `solution/` | Referans çözüm / Reference solution |
| `docs/` | Ders ve ödev dokümanları (MD + PDF) / Lesson & exercise docs |
