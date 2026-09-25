# 22 · Kubernetes / Kubernetes

🇹🇷 [Ders notları](docs/tr/ders.md) ([PDF](docs/tr/ders.pdf)) · [Ödevler](docs/tr/odevler.md) ([PDF](docs/tr/odevler.pdf))
🇬🇧 [Lesson notes](docs/en/lesson.md) ([PDF](docs/en/lesson.pdf)) · [Exercises](docs/en/exercises.md) ([PDF](docs/en/exercises.pdf))

## 🇹🇷 Bu modülde

- kind ile yerel küme (`scripts/kind-up.sh`), image'ı kind'a yükleme
- Deployment, Service, ConfigMap/Secret generator'ları, PostgreSQL StatefulSet
- Liveness/readiness/startup probe'ları (Actuator), requests/limits, graceful shutdown
- Kustomize base + dev/prod overlay'leri, HPA, rolling update ve rollback
- Manifest kuralları için bir test (`ManifestPolicyTest`, küme gerekmez)

## 🇬🇧 In this module

- A local cluster with kind (`scripts/kind-up.sh`), loading the image into kind
- Deployment, Service, ConfigMap/Secret generators, a PostgreSQL StatefulSet
- Liveness/readiness/startup probes (Actuator), requests/limits, graceful shutdown
- Kustomize base + dev/prod overlays, HPA, rolling update and rollback
- A test for manifest rules (`ManifestPolicyTest`, no cluster needed)

## Çalıştırma / How to run

Ön koşul / Prerequisite: JDK 27, Docker — `export JAVA_HOME=$(/usr/libexec/java_home -v 27)`

```bash
# Ders kodunu çalıştır — gereken altyapı Docker Compose ile otomatik başlar
# Run the lesson — required infrastructure starts automatically via Docker Compose
./mvnw -pl modules/22-kubernetes/lesson spring-boot:run

# Altyapı zaten çalışıyorsa (docker compose --profile ... up -d) / If infrastructure is already running
./mvnw -pl modules/22-kubernetes/lesson spring-boot:run -Dspring-boot.run.arguments=--spring.docker.compose.enabled=false

# Testler / Tests (Testcontainers)
./mvnw -pl modules/22-kubernetes/lesson verify

# Ödevler / Exercises (kırmızı başlar / start red)
./mvnw -Pexercises -pl modules/22-kubernetes/exercise test
```

```bash
# Kubernetes (kind): küme, deploy, temizlik / cluster, deploy, clean up
scripts/kind-up.sh
modules/22-kubernetes/deploy.sh dev        # veya / or: prod (HPA)
kubectl -n bookstore get pods
scripts/kind-down.sh
```

HTTP örnekleri / HTTP examples: [requests.http](requests.http)

## Yapı / Layout

| Klasör / Folder | İçerik / Content |
|---|---|
| `lesson/` | Çalışan örnekler + testler / Runnable examples + tests |
| `exercise/` | TODO'lu başlangıç kodu / Starter code with TODOs |
| `solution/` | Referans çözüm / Reference solution |
| `docs/` | Ders ve ödev dokümanları (MD + PDF) / Lesson & exercise docs |
