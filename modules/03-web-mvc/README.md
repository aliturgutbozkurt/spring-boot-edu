# 03 · Web MVC ile REST API / REST APIs with Web MVC

🇹🇷 [Ders notları](docs/tr/ders.md) ([PDF](docs/tr/ders.pdf)) · [Ödevler](docs/tr/odevler.md) ([PDF](docs/tr/odevler.pdf))
🇬🇧 [Lesson notes](docs/en/lesson.md) ([PDF](docs/en/lesson.pdf)) · [Exercises](docs/en/exercises.md) ([PDF](docs/en/exercises.pdf))

## 🇹🇷 Bu modülde

- CRUD REST API, doğru durum kodları, `201 + Location`
- Bean Validation (`@ISBN` dahil), parametre doğrulama
- RFC 9457 `ProblemDetail` hata yanıtları
- Sayfalama ve sıralama
- Spring Framework 7 API versiyonlama (`API-Version` başlığı)
- Content negotiation (JSON / CSV), Jackson 3 `@JacksonComponent`
- springdoc OpenAPI + Swagger UI
- Virtual thread'lerde Tomcat, `MockMvcTester`, `RestTestClient`

## 🇬🇧 In this module

- CRUD REST API, correct status codes, `201 + Location`
- Bean Validation (incl. `@ISBN`), parameter validation
- RFC 9457 `ProblemDetail` error responses
- Paging and sorting
- Spring Framework 7 API versioning (`API-Version` header)
- Content negotiation (JSON / CSV), Jackson 3 `@JacksonComponent`
- springdoc OpenAPI + Swagger UI
- Tomcat on virtual threads, `MockMvcTester`, `RestTestClient`

## Çalıştırma / How to run

Ön koşul / Prerequisite: JDK 27 — `export JAVA_HOME=$(/usr/libexec/java_home -v 27)`. Bu modül Docker gerektirmez. / This module does not need Docker.

```bash
# Uygulamayı başlat / Start the application (http://localhost:8080)
./mvnw -pl modules/03-web-mvc/lesson -am spring-boot:run

# Swagger UI
open http://localhost:8080/swagger-ui.html

# Testler / Tests (web slice + real-server IT)
./mvnw -pl modules/03-web-mvc/lesson -am verify

# Ödevler / Exercises (kırmızı başlar / start red)
./mvnw -Pexercises -pl modules/03-web-mvc/exercise -am test
```

HTTP örnekleri / HTTP examples: [requests.http](requests.http)

## Yapı / Layout

| Klasör / Folder | İçerik / Content |
|---|---|
| `lesson/` | Çalışan örnekler + testler / Runnable examples + tests |
| `exercise/` | TODO'lu başlangıç kodu / Starter code with TODOs |
| `solution/` | Referans çözüm / Reference solution |
| `docs/` | Ders ve ödev dokümanları (MD + PDF) / Lesson & exercise docs |
