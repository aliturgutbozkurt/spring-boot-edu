---
title: "Modül 04 — HTTP İstemcileri ve Dayanıklılık"
subtitle: "Ödevler"
module: "04-http-clients-resilience"
lang: tr-TR
date: "2026-09-23"
---

# Nasıl Çalışılır?

1. Başlangıç kodu `modules/04-http-clients-resilience/exercise/` içindedir. `TODO` yorumlarını bulun.
2. Her ödevin testleri hazırdır ve siz çözene kadar **kırmızıdır**. İş ortağı servislerini tek bir WireMock sunucusu taklit eder (`PartnerServerTest`).
3. Testleri çalıştırın:

```bash
./mvnw -Pexercises -pl modules/04-http-clients-resilience/exercise -am test
```

4. Tüm testler yeşil olduğunda ödev tamamdır.
5. Takıldığınızda önce ipuçlarına, sonra `modules/04-http-clients-resilience/solution/` içindeki çözüme bakın.

> [!TIP]
> Tek bir ödevin testini çalıştırmak için: `-Dtest=Exercise1Test -Dsurefire.failIfNoSpecifiedTests=false`

# Ödev 1 — Yorum Servisi İstemcisi (Kolay)

**Hedef:** Kod yazmadan, yalnızca bir arayüzle HTTP istemcisi tanımlamak.

İş ortağının yorum servisi iki uç nokta sunar: `GET /reviews/{isbn}` (yorum listesi) ve `POST /reviews/{isbn}` (JSON gövdeli yeni yorum).

**Yapılacaklar** (`exercise1` paketi):

- `TODO 1a` — `ReviewApi`'nin tüm metotları `/reviews` yolu altında olsun.
- `TODO 1b` — `forBook(isbn)`: `GET /reviews/{isbn}`.
- `TODO 1c` — `add(isbn, review)`: `POST /reviews/{isbn}`, yorum JSON gövde olarak gitsin.
- `TODO 1d` — `ReviewApi`'yi `reviews` grubunda bir HTTP servis istemcisi olarak kaydedin. Adres `spring.http.serviceclient.reviews.base-url`'den gelir.

**İpuçları:**

- Ders bölüm 3.4: `@HttpExchange`, `@GetExchange`, `@PostExchange`, `@PathVariable`, `@RequestBody`.
- `@ImportHttpServices(group = ..., types = ...)`.

**Kabul kriterleri:** `Exercise1Test` içindeki 2 testin ikisi de geçer.

**Tahmini süre:** 20 dakika

# Ödev 2 — Tekrar Deneme ve Yedek Cevap (Orta)

**Hedef:** Geçici hataları tekrar denemek, kalıcı hatalarda ise uygulamayı düşürmeden bir yedek cevap vermek.

Stok servisi bazen `502` veya `503` döner. Stok bilinmiyorsa sipariş sayfası "stok bilgisi yok" göstermelidir.

**Yapılacaklar:**

- `TODO 2a` — `ResilienceConfiguration`: Spring Framework 7'nin dayanıklılık özelliklerini açın. Ödev 3 de buna ihtiyaç duyar.
- `TODO 2b` — `StockClient.available`: yalnızca sunucu hatalarını (`5xx`) en fazla 2 kez, 50 ms arayla tekrar deneyin.
- `TODO 2c` — `StockFacade.availableOrUnknown`: denemeler bittikten sonra da hata sürerse exception fırlatmak yerine `UNKNOWN` döndürün.

**İpuçları:**

- Ders bölüm 3.6: `@EnableResilientMethods`, `@Retryable(includes = ..., maxRetries = ..., delay = ...)`.
- `RestClient`'ın `5xx` için fırlattığı exception: `HttpServerErrorException`. Tüm istemci hatalarının atası: `RestClientException`.
- Tekrar denenen metot (`StockClient`) ile yedek cevabı veren metot (`StockFacade`) neden ayrı bean'lerde?

**Kabul kriterleri:** `Exercise2Test` içindeki 3 testin hepsi geçer. Testler gelen istek sayısını da doğrular (1 + 2 deneme, `404` için tek istek).

**Tahmini süre:** 30 dakika

# Ödev 3 — İş Ortağını Aşırı Yükten Korumak (Zor)

**Hedef:** Eşzamanlılık sınırının iki politikasını (`BLOCK` ve `REJECT`) kullanmak ve reddedilen bir çağrıyı zarifçe karşılamak.

Kargo iş ortağı aynı anda en fazla **1 ekspres** ve **2 standart** fiyat sorgusuna izin veriyor. Ekspres slot doluysa müşteriyi bekletmek yerine hemen standart fiyat gösterilmeli.

**Yapılacaklar** (`exercise3` paketi):

- `TODO 3a` — `QuoteService.expressQuote`: aynı anda en fazla 1 çağrı. İkinci çağıran beklemeden **reddedilsin**.
- `TODO 3b` — `QuoteService.standardQuote`: aynı anda en fazla 2 çağrı, fazlası beklesin.
- `TODO 3c` — `QuoteFacade.expressOrStandard`: ekspres çağrı reddedilirse standart fiyatı alıp `"standard"` döndürsün.

**İpuçları:**

- Ders bölüm 3.7: `@ConcurrencyLimit(limit = 1, policy = ConcurrencyLimit.ThrottlePolicy.REJECT)`.
- Reddedilen çağrı `org.springframework.resilience.InvocationRejectedException` fırlatır.

**Kabul kriterleri:** `Exercise3Test` içindeki 2 testin ikisi de geçer.

**Tahmini süre:** 40 dakika

# Ek Meydan Okuma (İsteğe Bağlı)

`StockClient`'a bir okuma zaman aşımı ekleyin: `spring.http.serviceclient` yerine `RestClient.Builder` üzerinden yalnızca bu istemci için 500 ms ayarlayın. WireMock'ta `withFixedDelay(2000)` ile zaman aşımının `UNKNOWN` cevabına dönüştüğünü gösteren bir test yazın.
