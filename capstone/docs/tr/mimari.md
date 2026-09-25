---
title: "Bitirme Projesi — Kitapçı Platformu"
subtitle: "Mimari ve Kararlar"
module: "capstone"
lang: tr-TR
date: "2026-09-25"
---

# 1. Hedef

Bitirme projesi kursun teknolojilerini çalışan tek bir sistemde birleştirir: Müşterilerin kitap aradığı, sipariş verdiği ve sistemin stoğu, arama indeksini ve bildirimleri tutarlı tuttuğu bir online kitapçı. Docker Compose üzerinde tek komutla ve Helm ile yerel bir Kubernetes kümesinde (kind) çalışır.

Başarı şudur (SPEC, başarı kriteri 6): `docker compose up --build` her şeyi başlatır; tek bir sipariş PostgreSQL, MongoDB, Elasticsearch, Redis, Kafka ve Hazelcast'e dokunur; uçtan uca bir test *sipariş → event → arama indeksi → cache* akışını kanıtlar.

# 2. Genel Bakış

```text
                         ┌───────────────────────────────┐
  client ── HTTPS ──────▶│ gateway  (Spring Cloud Gateway)│── rate limit ──▶ Redis
                         │ JWT check · routing           │
                         └──┬──────────────┬──────────┬──┘
                            │ REST         │ REST     │ REST
                            ▼              ▼          ▼
                   ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
                   │ order-service│ │catalog-service│ │search-service│
                   │ PostgreSQL   │ │ MongoDB       │ │ Elasticsearch│
                   │ JPA · outbox │ │ Hazelcast lock│ │ Redis cache  │
                   └──┬───────┬───┘ └──▲─────────┬──┘ └──────▲───────┘
                      │ gRPC  └────────┘         │           │
                      │ ReserveStock             │           │
                      ▼ outbox relay             ▼           │
                 ┌──────────────────────── Kafka ────────────┴──┐
                 │ bookstore.orders  (OrderPlaced)               │
                 │ bookstore.catalog (BookChanged)               │
                 └───────────────────────────────────────────────┘
        all services ── OTLP (metrics, traces) ──▶ Grafana LGTM
```

| Servis | Sahip olduğu | Teknolojiler | Kurs modülleri |
|---|---|---|---|
| `gateway` | giriş noktası | Spring Cloud Gateway, JWT resource server, Redis rate limiter | 12, 23 |
| `order-service` | siparişler | PostgreSQL üzerinde Spring Data JPA, Flyway, transactional outbox, gRPC istemcisi | 06, 11, 21 |
| `catalog-service` | kitaplar ve stok | Spring Data MongoDB, gRPC sunucusu, Hazelcast `IMap` kilidi, Kafka producer | 07, 09, 11, 21 |
| `search-service` | arama okuma modeli | Spring Data Elasticsearch, Redis cache, Kafka consumer | 08, 10, 11 |
| `contracts` (kütüphane) | ortak sözleşmeler | `.proto` dosyaları, event record'ları | 21 |

Her servisin kendi veritabanı vardır. Hiçbir servis başka bir servisin veritabanını okumaz.

# 3. Ana Akışlar

## 3.1 Sipariş Vermek

```text
client     gateway      order-service          catalog-service      Kafka     search
  │ POST /api/orders                                                               
  │───────────────▶│ check JWT   │                      │                │           │
  │                │────────────▶│ ReserveStock (gRPC)  │                │           │
  │                │             │─────────────────────▶│ lock ISBN      │           │
  │                │             │                      │ stock -= qty   │           │
  │                │             │◀──── reserved ───────│ unlock         │           │
  │                │             │ BEGIN                │                │           │
  │                │             │  INSERT order        │                │           │
  │                │             │  INSERT outbox       │                │           │
  │                │             │ COMMIT               │                │           │
  │◀──── 201 ──────│◀────────────│                      │                │           │
  │                │             │ relay ─── OrderPlaced ───────────────▶│           │
  │                │             │                      │                │──────────▶│ sold += qty
  │                │             │                      │                │           │ evict cache
```

1. Gateway JWT'yi kontrol eder ve isteği iletir.
2. Sipariş servisi stoğu gRPC üzerinden **senkron** olarak ayırır: Müşteri kitabın mevcut olup olmadığını hemen bilmelidir.
3. Katalog servisi ISBN'i Hazelcast'te kilitler, böylece iki sipariş son kopyayı iki kez satamaz (birden çok katalog örneğiyle de).
4. Sipariş ve bir outbox satırı **tek** bir transaction'da yazılır. Bir relay outbox satırlarını Kafka'ya yayınlar.
5. Tüketiciler bağımsız olarak tepki verir: Arama servisi kitabın satış sayılarını günceller ve cache'ini boşaltır; sipariş servisinin bildirim listener'ı onayı kaydeder.

## 3.2 Kataloğu Değiştirmek

Katalog servisi bir kitap oluşturulduğunda veya değiştirildiğinde (başlangıçtaki örnek verisi için de) `bookstore.catalog`'a `BookChanged` yayınlar. Arama servisi bu event'leri tüketir ve kitabı (yeniden) indeksler. Arama indeksi bir **okuma modelidir**: Event'lerden her zaman yeniden oluşturulabilir.

## 3.3 Arama

`GET /api/search?q=…` gateway üzerinden arama servisine gider. Sonuçlar kısa bir süre Redis'te önbelleğe alınır. İndeks güncellemeleri ilgili kayıtları boşaltır.

# 4. Mimari Karar Kayıtları (ADR)

## ADR-1: Dört Servis ve Bir Gateway

- **Bağlam:** Kurs dağıtık iletişimi (REST, gRPC, Kafka) ve birden çok veritabanını göstermeli, ama bir öğrenci her şeyi yine de bir dizüstü bilgisayarda çalıştırabilmelidir.
- **Karar:** Gateway + `order-service`, `catalog-service`, `search-service`, artı paylaşılan bir `contracts` kütüphanesi (SPEC karar 13).
- **Sonuçlar:** Her teknolojinin net bir sahibi vardır. Dört JVM artı altyapı yaklaşık 8 GB Docker belleğine ihtiyaç duyar. Modüler bir monolit (modül 18) işletmek için daha basit olurdu; bitirme projesi, sorunlarını pratik etmek için bilerek servisleri seçer.

## ADR-2: gRPC ile Senkron Stok Rezervasyonu

- **Bağlam:** Bir sipariş yalnızca kitap stoktayken kabul edilebilir.
- **Karar:** `order-service`, 2 saniyelik bir deadline ile gRPC üzerinden `catalog-service`'in `ReserveStock`'unu çağırır.
- **Alternatifler:** Asenkron bir saga (event ile ayır, sonra onayla veya iptal et) daha iyi ölçeklenir ve katalog kesintilerinden sağ çıkar, ama müşteriyi ikinci bir cevap için bekletir.
- **Sonuçlar:** Sipariş servisi sipariş anında kataloğa bağımlıdır. Deadline, bir katalog kesintisini asılı kalan istekler yerine net bir `503`'e çevirir; kısa bir yeniden başlatma, idempotent rezervasyonu yeniden deneyerek atlatılır (ödev 3). Bir circuit breaker (modül 23) ayrıca kapalı kalan bir kataloğu çağırmayı bırakırdı.

## ADR-3: OrderPlaced için Transactional Outbox

- **Bağlam:** Siparişi yazmak ve event'i yayınlamak birbirinden kopmamalıdır (modül 11).
- **Karar:** Event, siparişin transaction'ında bir `outbox` tablosuna yazılır; zamanlanmış bir relay onu yayınlar ve işaretler.
- **Sonuçlar:** En az bir kez iletim; tüketiciler idempotent olmalıdır (arama servisi güncellemeleri sipariş ID'sine göre anahtarlar).

## ADR-4: Servis Başına Bir Veritabanı, Okuma Modeli Olarak Arama

- **Karar:** Siparişler için PostgreSQL (transaction'lar, kısıtlar), katalog için MongoDB (esnek kitap dokümanları), Elasticsearch yalnızca event'lerle beslenen türetilmiş bir okuma modeli olarak.
- **Sonuçlar:** Servisler arası join yok; diğer servislerin ihtiyaç duyduğu veri event'lerle veya gRPC yanıtlarıyla taşınır.

## ADR-5: Gateway'de ve Servislerde JWT

- **Karar:** Gateway bearer token'ı doğrular ve iletir; her servis de bir resource server'dır (derinlemesine savunma). Yerel sistem için gateway, demo kullanıcılarla bir geliştirme token endpoint'i sunar ve token'lar ortamdan gelen bir HMAC anahtarıyla imzalanır.
- **Production:** Asimetrik anahtarlı (JWKS) bir yetkilendirme sunucusu (Spring Authorization Server, modül 12, veya Keycloak).

## ADR-6: Stok için Hazelcast IMap Kilidi

- **Karar:** Stok güncellemesinin etrafında `IMap.tryLock(isbn)` (SPEC karar 10: CP subsystem yalnızca Enterprise'da).
- **Sonuçlar:** Birden çok katalog örneğiyle doğru çalışır; kilidin bir kira süresi vardır, böylece çöken bir örnek bir ISBN'i sonsuza kadar kilitleyemez.

## ADR-7: Cache ve Rate Limiting için Redis

- **Karar:** Arama servisi arama sonuçlarını Redis'te önbelleğe alır; gateway'in rate limiter'ı token bucket'larını Redis'te tutar.
- **Sonuçlar:** Bir altyapı servisi daha, ama iki kullanım da örnekler arasında paylaşılan duruma ihtiyaç duyar.

## ADR-8: Docker Compose ve Helm ile Deployment

- **Karar:** Tüm sistem için tek bir `capstone/compose.yaml` ve kind için bir Helm chart. Image'lar modül 20'nin çok aşamalı jlink Dockerfile'ını kullanır.
- **Sonuçlar:** Aynı image'lar iki ortamda da çalışır; yapılandırma ortam değişkenlerinden gelir (12-factor).

# 5. Gözlemlenebilirlik

Tüm servisler metrikleri ve trace'leri OTLP ile Grafana LGTM'ye aktarır (modül 15). Trace bağlamı HTTP, gRPC ve Kafka üzerinden taşınır, böylece bir sipariş gateway'den arama indeksine kadar tek bir trace'tir.

# 6. Proje Yapısı

```text
capstone/
  pom.xml                 aggregator
  contracts/              .proto dosyaları ve event record'ları (kütüphane)
  gateway/                Spring Cloud Gateway
  order-service/
  catalog-service/
  search-service/
  e2e-tests/              uçtan uca test (Testcontainers)
  Dockerfile              dört servis için tek imaj tarifi (jar kaynak koddan ya da makineden)
  compose.yaml, up.sh     tüm sistem
  k8s/helm/bookstore/     Helm chart; k8s/deploy-kind.sh onu kind'a kurar
  exercise/, solution/    bitirme projesi ödevleri (her biri bir order-service kopyası)
  requests.http           gateway üzerinden HTTP örnekleri
  docs/                   bu doküman, rehber ve ödevler (TR + EN, PDF)
```
