# 24 · Spring AI / Spring AI

🇹🇷 [Ders notları](docs/tr/ders.md) ([PDF](docs/tr/ders.pdf)) · [Ödevler](docs/tr/odevler.md) ([PDF](docs/tr/odevler.pdf))
🇬🇧 [Lesson notes](docs/en/lesson.md) ([PDF](docs/en/lesson.pdf)) · [Exercises](docs/en/exercises.md) ([PDF](docs/en/exercises.pdf))

## 🇹🇷 Bu modülde

- Ollama ile lokal LLM (compose `ai` profili, küçük model), `ChatClient` ve prompt şablonları
- Structured output (JSON → record), JDBC ile chat memory
- pgvector ile RAG: kitap açıklamaları için ETL pipeline, `QuestionAnswerAdvisor`
- Tool calling (stok sorgusu) ve aynı tool'u MCP server olarak sunmak
- LLM'siz testler: sahte `ChatModel` ve anahtar kelime tabanlı `EmbeddingModel`

## 🇬🇧 In this module

- A local LLM with Ollama (compose profile `ai`, a small model), `ChatClient` and prompt templates
- Structured output (JSON → record), chat memory with JDBC
- RAG with pgvector: an ETL pipeline for the book descriptions, `QuestionAnswerAdvisor`
- Tool calling (a stock query) and the same tool offered as an MCP server
- Tests without an LLM: a fake `ChatModel` and a keyword-based `EmbeddingModel`

## Çalıştırma / How to run

Ön koşul / Prerequisite: JDK 27, Docker — `export JAVA_HOME=$(/usr/libexec/java_home -v 27)`

```bash
# Ders kodunu çalıştır — gereken altyapı Docker Compose ile otomatik başlar
# Run the lesson — required infrastructure starts automatically via Docker Compose
./mvnw -pl modules/24-spring-ai/lesson spring-boot:run

# Altyapı zaten çalışıyorsa (docker compose --profile ... up -d) / If infrastructure is already running
./mvnw -pl modules/24-spring-ai/lesson spring-boot:run -Dspring-boot.run.arguments=--spring.docker.compose.enabled=false

# Testler / Tests (Testcontainers)
./mvnw -pl modules/24-spring-ai/lesson verify

# Ödevler / Exercises (kırmızı başlar / start red)
./mvnw -Pexercises -pl modules/24-spring-ai/exercise test
```

İlk başlangıç Ollama modellerini indirir (~1,6 GB) / The first start downloads the Ollama models (~1.6 GB).
Modeller / Models: `qwen3:1.7b` (chat), `nomic-embed-text` (embeddings) — `application.yaml`.

HTTP örnekleri / HTTP examples: [requests.http](requests.http)

## Yapı / Layout

| Klasör / Folder | İçerik / Content |
|---|---|
| `lesson/` | Çalışan örnekler + testler / Runnable examples + tests |
| `exercise/` | TODO'lu başlangıç kodu / Starter code with TODOs |
| `solution/` | Referans çözüm / Reference solution |
| `docs/` | Ders ve ödev dokümanları (MD + PDF) / Lesson & exercise docs |
