# 00 · Kurulum ve Modern Java (21 → 27) / Setup and Modern Java (21 → 27)

🇹🇷 [Ders notları](docs/tr/ders.md) ([PDF](docs/tr/ders.pdf)) · [Ödevler](docs/tr/odevler.md) ([PDF](docs/tr/odevler.pdf))
🇬🇧 [Lesson notes](docs/en/lesson.md) ([PDF](docs/en/lesson.pdf)) · [Exercises](docs/en/exercises.md) ([PDF](docs/en/exercises.pdf))

## 🇹🇷 Bu modülde

- JDK 27, Docker ve IDE kurulumu, repo ve ödev akışı
- Records, sealed types, pattern matching, record patterns
- Text blocks, `var`, sequenced collections, stream gatherers
- Virtual threads ve scoped values
- Compact source files (`void main` + `IO.println`)
- Structured concurrency (preview, `-Ppreview` profili)

## 🇬🇧 In this module

- Installing JDK 27, Docker and an IDE; the repository and exercise workflow
- Records, sealed types, pattern matching, record patterns
- Text blocks, `var`, sequenced collections, stream gatherers
- Virtual threads and scoped values
- Compact source files (`void main` + `IO.println`)
- Structured concurrency (preview, `-Ppreview` profile)

## Çalıştırma / How to run

Ön koşul / Prerequisite: JDK 27 — `export JAVA_HOME=$(/usr/libexec/java_home -v 27)`. Bu modül Docker gerektirmez. / This module does not need Docker.

```bash
# Tüm örnekleri ders sırasıyla çalıştır / Run every example in lesson order
./mvnw -pl modules/00-setup-modern-java/lesson spring-boot:run

# Tek dosyalık program / Single-file program
(cd modules/00-setup-modern-java/lesson && java src/scripts/HelloBookstore.java Ayşe)

# Testler, preview dahil / Tests, including preview
./mvnw -pl modules/00-setup-modern-java/lesson verify
./mvnw -Ppreview -pl modules/00-setup-modern-java/lesson verify

# Ödevler / Exercises (kırmızı başlar / start red)
./mvnw -Pexercises -pl modules/00-setup-modern-java/exercise test
```

## Yapı / Layout

| Klasör / Folder | İçerik / Content |
|---|---|
| `lesson/` | Çalışan örnekler + testler / Runnable examples + tests |
| `lesson/src/preview/` | Preview özellikler / Preview features (`-Ppreview`) |
| `lesson/src/scripts/` | Compact source file örneği / Compact source file example |
| `exercise/` | TODO'lu başlangıç kodu / Starter code with TODOs |
| `solution/` | Referans çözüm / Reference solution |
| `docs/` | Ders ve ödev dokümanları (MD + PDF) / Lesson & exercise docs |
