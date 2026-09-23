# 02 · Konfigürasyon ve Auto-Configuration / Configuration and Auto-Configuration

🇹🇷 [Ders notları](docs/tr/ders.md) ([PDF](docs/tr/ders.pdf)) · [Ödevler](docs/tr/odevler.md) ([PDF](docs/tr/odevler.pdf))
🇬🇧 [Lesson notes](docs/en/lesson.md) ([PDF](docs/en/lesson.pdf)) · [Exercises](docs/en/exercises.md) ([PDF](docs/en/exercises.pdf))

## 🇹🇷 Bu modülde

- Ayar kaynakları, öncelik sırası ve relaxed binding
- Doğrulanan `@ConfigurationProperties` record'ları, `@Value`
- Profiller, profil grupları, `on-profile` belgeleri, `spring.config.import`
- Kendi starter'ınızı yazmak (autoconfigure + starter modülleri)
- Conditions report ile hata ayıklama

## 🇬🇧 In this module

- Property sources, their order and relaxed binding
- Validated `@ConfigurationProperties` records, `@Value`
- Profiles, profile groups, `on-profile` documents, `spring.config.import`
- Writing your own starter (autoconfigure + starter modules)
- Debugging with the conditions report

## Çalıştırma / How to run

Ön koşul / Prerequisite: JDK 27 — `export JAVA_HOME=$(/usr/libexec/java_home -v 27)`. Bu modül Docker gerektirmez. / This module does not need Docker.

Starter aynı build'de derlendiği için komutlarda `-am` vardır. / The starter is built in the same reactor, hence `-am`.

```bash
# Tüm örnekler / Every example
./mvnw -pl modules/02-configuration/lesson -am spring-boot:run

# Profil grubu ve komut satırı ezmesi / Profile group and a command-line override
./mvnw -pl modules/02-configuration/lesson -am spring-boot:run \
  -Dspring-boot.run.arguments="--spring.profiles.active=local --bookstore.store.name=Komut"

# Conditions report
./mvnw -pl modules/02-configuration/lesson -am spring-boot:run -Dspring-boot.run.arguments="--debug"

# Testler / Tests
./mvnw -pl modules/02-configuration/lesson -am verify

# Ödevler / Exercises (kırmızı başlar / start red)
./mvnw -Pexercises -pl modules/02-configuration/exercise -am test
```

## Yapı / Layout

| Klasör / Folder | İçerik / Content |
|---|---|
| `lesson/` | Çalışan örnekler + testler / Runnable examples + tests |
| `starter/bookstore-greeting-autoconfigure/` | Starter'ın kodu ve koşulları / The starter's code and conditions |
| `starter/bookstore-greeting-spring-boot-starter/` | Yalnızca bağımlılıklar / Dependencies only |
| `exercise/` | TODO'lu başlangıç kodu / Starter code with TODOs |
| `solution/` | Referans çözüm / Reference solution |
| `docs/` | Ders ve ödev dokümanları (MD + PDF) / Lesson & exercise docs |
