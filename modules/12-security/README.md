# 12 · Spring Security ile Güvenlik / Security with Spring Security

🇹🇷 [Ders notları](docs/tr/ders.md) ([PDF](docs/tr/ders.pdf)) · [Ödevler](docs/tr/odevler.md) ([PDF](docs/tr/odevler.pdf))
🇬🇧 [Lesson notes](docs/en/lesson.md) ([PDF](docs/en/lesson.pdf)) · [Exercises](docs/en/exercises.md) ([PDF](docs/en/exercises.pdf))

## 🇹🇷 Bu modülde

- `SecurityFilterChain` anatomisi: API ve tarayıcı için ayrı zincirler
- PostgreSQL'de kullanıcılar (`JdbcUserDetailsManager`), BCrypt ile parola saklama
- Form login, HTTP Basic, CSRF, CORS
- Method security (`@PreAuthorize`, `@PostAuthorize`)
- Spring Authorization Server ile JWT almak, resource server olarak JWT doğrulamak
- OAuth2 login (GitHub), `spring-security-test` ile testler

## 🇬🇧 In this module

- The anatomy of a `SecurityFilterChain`: separate chains for the API and the browser
- Users in PostgreSQL (`JdbcUserDetailsManager`), password storage with BCrypt
- Form login, HTTP Basic, CSRF, CORS
- Method security (`@PreAuthorize`, `@PostAuthorize`)
- Getting a JWT from Spring Authorization Server, validating it as a resource server
- OAuth2 login (GitHub), tests with `spring-security-test`

## Çalıştırma / How to run

Ön koşul / Prerequisite: JDK 27, Docker — `export JAVA_HOME=$(/usr/libexec/java_home -v 27)`

```bash
# Ders kodunu çalıştır — gereken altyapı Docker Compose ile otomatik başlar
# Run the lesson — required infrastructure starts automatically via Docker Compose
./mvnw -pl modules/12-security/lesson spring-boot:run

# Altyapı zaten çalışıyorsa (docker compose --profile ... up -d) / If infrastructure is already running
./mvnw -pl modules/12-security/lesson spring-boot:run -Dspring-boot.run.arguments=--spring.docker.compose.enabled=false

# Testler / Tests (Testcontainers)
./mvnw -pl modules/12-security/lesson verify

# Ödevler / Exercises (kırmızı başlar / start red)
./mvnw -Pexercises -pl modules/12-security/exercise test
```

HTTP örnekleri / HTTP examples: [requests.http](requests.http)

## Yapı / Layout

| Klasör / Folder | İçerik / Content |
|---|---|
| `lesson/` | Çalışan örnekler + testler / Runnable examples + tests |
| `exercise/` | TODO'lu başlangıç kodu / Starter code with TODOs |
| `solution/` | Referans çözüm / Reference solution |
| `docs/` | Ders ve ödev dokümanları (MD + PDF) / Lesson & exercise docs |
