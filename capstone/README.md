# Capstone · Kitapçı Platformu / Bookstore Platform

🇹🇷 [Rehber](docs/tr/ders.md) ([PDF](docs/tr/ders.pdf)) · [Ödevler](docs/tr/odevler.md) ([PDF](docs/tr/odevler.pdf)) · [Mimari](docs/tr/mimari.md) ([PDF](docs/tr/mimari.pdf))
🇬🇧 [Guide](docs/en/lesson.md) ([PDF](docs/en/lesson.pdf)) · [Exercises](docs/en/exercises.md) ([PDF](docs/en/exercises.pdf)) · [Architecture](docs/en/architecture.md) ([PDF](docs/en/architecture.pdf))

## 🇹🇷 Bu projede

- `gateway`: Spring Cloud Gateway — tek giriş, JWT kontrolü, Redis ile rate limit, geliştirme token endpoint'i
- `order-service`: PostgreSQL/JPA siparişler, transactional outbox → Kafka, gRPC ile stok rezervasyonu
- `catalog-service`: MongoDB katalog, gRPC stok servisi, Hazelcast `IMap` kilidi, `BookChanged` event'leri
- `search-service`: Kafka event'lerinden beslenen Elasticsearch okuma modeli, Redis cache
- `contracts`: `.proto` ve event record'ları; `e2e-tests`: tüm platformun uçtan uca testi
- Docker Compose ile tek komut, Grafana LGTM ile gözlemlenebilirlik, kind üzerinde Helm chart

## 🇬🇧 In this project

- `gateway`: Spring Cloud Gateway — single entry point, JWT check, rate limiting with Redis, a development token endpoint
- `order-service`: orders with PostgreSQL/JPA, transactional outbox → Kafka, stock reservation over gRPC
- `catalog-service`: the catalog in MongoDB, a gRPC stock service, a Hazelcast `IMap` lock, `BookChanged` events
- `search-service`: an Elasticsearch read model fed by Kafka events, a Redis cache
- `contracts`: `.proto` files and event records; `e2e-tests`: the end-to-end test of the whole platform
- One command with Docker Compose, observability with Grafana LGTM, a Helm chart on kind

## Çalıştırma / How to run

Ön koşul / Prerequisite: JDK 27, Docker (≈ 6 GB bellek / memory) — `export JAVA_HOME=$(/usr/libexec/java_home -v 27)`

```bash
# Tek komut: imajlar kaynak koddan Docker içinde derlenir / One command: images are built from source inside Docker
cd capstone && docker compose up --build

# Daha hızlı: jar'lar makinede derlenir / Faster: the jars are built on the host
capstone/up.sh
docker compose -f capstone/compose.yaml down -v

# Testler (servisler + uçtan uca) / Tests (services + end-to-end)
./mvnw -pl capstone/contracts,capstone/catalog-service,capstone/order-service,capstone/search-service,capstone/gateway,capstone/e2e-tests verify

# Ödevler / Exercises (kırmızı başlar / start red)
./mvnw -Pexercises -pl capstone/exercise/order-service -am test

# Kubernetes (kind + Helm)
capstone/k8s/deploy-kind.sh                                  # küçük disk / small disk: --set infra.lgtm.enabled=false
kubectl -n bookstore port-forward svc/gateway 8080:8080
```

Gateway: http://localhost:8080 · Grafana: http://localhost:3000 · HTTP örnekleri / HTTP examples: [requests.http](requests.http)

## Yapı / Layout

| Klasör / Folder | İçerik / Content |
|---|---|
| `contracts/`, `*-service/`, `gateway/` | Platformun servisleri / The services of the platform |
| `e2e-tests/` | Uçtan uca test (Testcontainers + compose) / End-to-end test |
| `exercise/order-service/` | TODO'lu ödev projesi / Exercise project with TODOs |
| `solution/order-service/` | Referans çözüm / Reference solution |
| `compose.yaml`, `Dockerfile`, `k8s/` | Çalıştırma ve deployment / Running and deployment |
| `docs/` | Rehber, ödevler, mimari (MD + PDF) / Guide, exercises, architecture |
