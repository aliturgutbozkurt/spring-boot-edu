---
title: "Modül 23 — Spring Cloud"
subtitle: "Ödevler"
module: "23-spring-cloud"
lang: tr-TR
date: "2026-09-25"
---

# Nasıl Çalışılır

1. Başlangıç kodu `modules/23-spring-cloud/exercise/` altındadır: Bir katalog ve bir yorum (review) servisinin önünde küçük bir gateway (Spring Cloud Gateway, WebFlux). `TODO` yorumlarını bulun (`application.yaml` içinde de).
2. Testler iki servis için WireMock kullanır, bu yüzden Docker gerekmez:

```bash
./mvnw -Pexercises -pl modules/23-spring-cloud/exercise -am test
```

3. Tüm testler yeşil olduğunda ödev tamamdır.
4. Takılırsanız önce ipuçlarını, sonra `modules/23-spring-cloud/solution/` altındaki çözümü okuyun.

# Ödev 1 — API Anahtarlı Yeni Bir Route (Orta)

**Hedef:** Gateway yorum servisini de sunar ve yalnızca geçerli bir API anahtarı olan istemciler geçer.

**Yapılacaklar:**

- `TODO 1a` (`application.yaml`) — Bir `reviews` route'u: `/api/reviews/**`, load balancer üzerinden `review-service` servisine.
- `TODO 1b` (`security.ApiKeyFilter`) — `/api/**`'a giden her istek geçerli bir `X-Api-Key` header'ına ihtiyaç duyar. Aksi hâlde `401` cevabı verin ve durun.

**İpuçları:**

- Ders bölüm 3.1: `uri: lb://…` ve bir `Path` predicate'i.
- Bir `GlobalFilter` içinde `exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)` ve `return exchange.getResponse().setComplete();` isteği bir servisi çağırmadan bitirir.
- Anahtar `bookstore.gateway.api-key`'den gelir (`dev-key`).

**Kabul kriterleri:** `Exercise1Test` içindeki 3 testin hepsi geçer.

**Tahmini süre:** 30 dakika

# Ödev 2 — Breaker Açılır (Orta)

**Hedef:** Yorum servisi başarısız olduğunda gateway boş, işaretli bir listeyle cevap verir ve tekrarlanan hatalardan sonra onu bir süre çağırmayı bırakır.

**Yapılacaklar:**

- `TODO 2a` (`application.yaml`) — Ödev 1'deki route'ta `reviews` adlı bir `CircuitBreaker` filtresi, `forward:/fallback/reviews` fallback'iyle. `503` ve `500` cevapları hata sayılır.
- `TODO 2b` (`fallback.FallbackController`) — `/fallback/reviews`, `{"reviews": [], "fallback": true}` cevabı verir.

**İpuçları:**

- Filtre argümanları: `name`, `fallbackUri`, `statusCodes` (bir liste). `statusCodes` olmadan yalnızca exception'lar ve timeout'lar sayılır ve servisten gelen bir `503` istemciye değişmeden gider.
- Breaker ayarları (`resilience4j.circuitbreaker.instances.reviews`) hazır verilmiştir: 4 çağrı, %50.
- `@RequestMapping("/fallback/reviews")` her HTTP metodunu kabul eder.

**Kabul kriterleri:** `Exercise2Test` içindeki iki test de geçer.

**Tahmini süre:** 30 dakika

# Ödev 3 — Anahtarı Yeniden Başlatmadan Değiştirmek (Kolay)

**Hedef:** Sızdırılmış bir API anahtarı yapılandırmada değiştirilir. Gateway yeni anahtarı `POST /actuator/refresh` sonrasında, yeniden başlatmadan kullanmalıdır.

**Yapılacaklar** (`security.ApiKeys`):

- `TODO 3` — Bean'in bir refresh'ten sonra yapılandırmayı yeniden okumasını sağlayın.

**İpuçları:**

- Ders bölüm 3.5: `@RefreshScope`.
- Test ortamı değiştirir ve `/actuator/refresh`'in yaptığı gibi `ContextRefresher.refresh()`'i çağırır.

**Kabul kriterleri:** `Exercise3Test` geçer.

**Tahmini süre:** 10 dakika

# Ek Meydan Okuma (İsteğe Bağlı)

Ödev gateway'ini dersin compose sisteminin önüne koyun: Onu 9100 portunda `compose.yaml`'ye ekleyin, yorum route'u küçük bir WireMock container'ına (`wiremock/wiremock`) yönelsin. Ardından WireMock container'ını durdurun ve breaker'ın açılmasını izleyin (`/actuator/circuitbreakers`).
