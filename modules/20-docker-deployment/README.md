# 20 · Docker ve Deployment / Docker and Deployment

🇹🇷 [Ders notları](docs/tr/ders.md) ([PDF](docs/tr/ders.pdf)) · [Ödevler](docs/tr/odevler.md) ([PDF](docs/tr/odevler.pdf))
🇬🇧 [Lesson notes](docs/en/lesson.md) ([PDF](docs/en/lesson.pdf)) · [Exercises](docs/en/exercises.md) ([PDF](docs/en/exercises.pdf))

## 🇹🇷 Bu modülde

- Çok aşamalı Dockerfile: katmanlı jar, jlink ile küçük JRE 27, root olmayan kullanıcı, `HEALTHCHECK`
- Buildpacks ile image (`spring-boot:build-image`) ve image boyut karşılaştırması (`image-sizes.sh`)
- Uygulama + veritabanı için Docker Compose (`compose.yaml`), `depends_on: service_healthy`
- Liveness/readiness probe'ları, graceful shutdown, 12-factor yapılandırma (ortam değişkenleri)

## 🇬🇧 In this module

- A multi-stage Dockerfile: layered jar, a small JRE 27 made with jlink, a non-root user, `HEALTHCHECK`
- An image with Buildpacks (`spring-boot:build-image`) and an image size comparison (`image-sizes.sh`)
- Docker Compose for application + database (`compose.yaml`), `depends_on: service_healthy`
- Liveness/readiness probes, graceful shutdown, 12-factor configuration (environment variables)

## Çalıştırma / How to run

Ön koşul / Prerequisite: JDK 27, Docker — `export JAVA_HOME=$(/usr/libexec/java_home -v 27)`

```bash
# Ders kodunu çalıştır — gereken altyapı Docker Compose ile otomatik başlar
# Run the lesson — required infrastructure starts automatically via Docker Compose
./mvnw -pl modules/20-docker-deployment/lesson spring-boot:run

# Altyapı zaten çalışıyorsa (docker compose --profile ... up -d) / If infrastructure is already running
./mvnw -pl modules/20-docker-deployment/lesson spring-boot:run -Dspring-boot.run.arguments=--spring.docker.compose.enabled=false

# Testler / Tests (Testcontainers)
./mvnw -pl modules/20-docker-deployment/lesson verify

# Ödevler / Exercises (kırmızı başlar / start red)
./mvnw -Pexercises -pl modules/20-docker-deployment/exercise test
```

```bash
# Container olarak / As a container (jar → image → app + PostgreSQL)
./mvnw -pl modules/20-docker-deployment/lesson package
docker compose -f modules/20-docker-deployment/compose.yaml up --build
docker compose -f modules/20-docker-deployment/compose.yaml down

# Image boyutları / Image sizes (naive, multi-stage + jlink, Buildpacks)
modules/20-docker-deployment/image-sizes.sh
```

HTTP örnekleri / HTTP examples: [requests.http](requests.http)

## Yapı / Layout

| Klasör / Folder | İçerik / Content |
|---|---|
| `lesson/` | Çalışan örnekler + testler / Runnable examples + tests |
| `exercise/` | TODO'lu başlangıç kodu / Starter code with TODOs |
| `solution/` | Referans çözüm / Reference solution |
| `docs/` | Ders ve ödev dokümanları (MD + PDF) / Lesson & exercise docs |
