# 01 · Spring Core Container: IoC ve Bağımlılık Enjeksiyonu / IoC and Dependency Injection

🇹🇷 [Ders notları](docs/tr/ders.md) ([PDF](docs/tr/ders.pdf)) · [Ödevler](docs/tr/odevler.md) ([PDF](docs/tr/odevler.pdf))
🇬🇧 [Lesson notes](docs/en/lesson.md) ([PDF](docs/en/lesson.pdf)) · [Exercises](docs/en/exercises.md) ([PDF](docs/en/exercises.pdf))

## 🇹🇷 Bu modülde

- Constructor ve setter injection, field injection neden kullanılmaz
- `@Component` / `@Bean`, `@Configuration` full ve lite modu
- Singleton ve prototype scope, `ObjectProvider`
- `@PostConstruct`, `@PreDestroy`, `SmartLifecycle`
- `@Profile`, `@ConditionalOnProperty`
- Spring Framework 7 `BeanRegistrar`
- `@EventListener` (sıralı ve koşullu)
- `@Aspect` ile süre ölçümü

## 🇬🇧 In this module

- Constructor and setter injection, and why not field injection
- `@Component` / `@Bean`, `@Configuration` full and lite mode
- Singleton and prototype scope, `ObjectProvider`
- `@PostConstruct`, `@PreDestroy`, `SmartLifecycle`
- `@Profile`, `@ConditionalOnProperty`
- Spring Framework 7 `BeanRegistrar`
- `@EventListener` (ordered and conditional)
- Timing with an `@Aspect`

## Çalıştırma / How to run

Ön koşul / Prerequisite: JDK 27 — `export JAVA_HOME=$(/usr/libexec/java_home -v 27)`. Bu modül Docker gerektirmez. / This module does not need Docker.

```bash
# Tüm örnekleri ders sırasıyla çalıştır / Run every example in lesson order
./mvnw -pl modules/01-core-container/lesson spring-boot:run

# dev profili ve farklı ayarlarla / With the dev profile and other settings
./mvnw -pl modules/01-core-container/lesson spring-boot:run \
  -Dspring-boot.run.arguments="--spring.profiles.active=dev --bookstore.recommendations.strategy=budget"

# Testler / Tests
./mvnw -pl modules/01-core-container/lesson verify

# Ödevler / Exercises (kırmızı başlar / start red)
./mvnw -Pexercises -pl modules/01-core-container/exercise test
```

## Yapı / Layout

| Klasör / Folder | İçerik / Content |
|---|---|
| `lesson/` | Çalışan örnekler + testler / Runnable examples + tests |
| `exercise/` | TODO'lu başlangıç kodu / Starter code with TODOs |
| `solution/` | Referans çözüm / Reference solution |
| `docs/` | Ders ve ödev dokümanları (MD + PDF) / Lesson & exercise docs |
