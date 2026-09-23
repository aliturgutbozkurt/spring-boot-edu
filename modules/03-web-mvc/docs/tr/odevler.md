---
title: "Modül 03 — Web MVC ile REST API"
subtitle: "Ödevler"
module: "03-web-mvc"
lang: tr-TR
date: "2026-09-23"
---

# Nasıl Çalışılır?

1. Başlangıç kodu `modules/03-web-mvc/exercise/` içindedir. `TODO` yorumlarını bulun (Java dosyaları ve `application.yaml`).
2. Her ödevin testleri hazırdır ve siz çözene kadar **kırmızıdır**. Bazı testler baştan yeşildir. Bunlar, çözümünüzün mevcut davranışı bozmadığını kontrol eden koruma testleridir.
3. Testleri çalıştırın:

```bash
./mvnw -Pexercises -pl modules/03-web-mvc/exercise -am test
```

4. Tüm testler yeşil olduğunda ödev tamamdır.
5. Takıldığınızda önce ipuçlarına, sonra `modules/03-web-mvc/solution/` içindeki çözüme bakın.

> [!TIP]
> Tek bir ödevin testini çalıştırmak için: `-Dtest=Exercise1Test -Dsurefire.failIfNoSpecifiedTests=false`

# Ödev 1 — Yazar Uç Noktası (Kolay)

**Hedef:** Doğru durum kodlarıyla küçük bir REST uç noktası yazmak.

`Author` ve üç örnek yazarı olan `AuthorRepository` hazırdır.

**Yapılacaklar** (`exercise1` paketi):

- `TODO 1a` — `GET /api/authors`: tüm yazarlar.
- `TODO 1b` — `GET /api/authors/{id}`: bir yazar. Yazar yoksa `404`.
- `TODO 1c` — `POST /api/authors`: doğrulanan bir `AuthorRequest` alsın, `201 Created`, `Location` başlığı ve yeni yazarı döndürsün.
- `TODO 1d` — `AuthorRequest`: ad boş olamaz. Ülke, tam olarak iki büyük harf olmalı (ör. `TR`).

**İpuçları:**

- Ders bölüm 3.1 ve 3.2.
- 404 için en kısa yol: `throw new ResponseStatusException(HttpStatus.NOT_FOUND, "...")`.
- İki büyük harf için: `@Pattern(regexp = "[A-Z]{2}")`.

**Kabul kriterleri:** `Exercise1Test` içindeki 5 testin hepsi geçer.

**Tahmini süre:** 30 dakika

# Ödev 2 — Sipariş Hataları ProblemDetail Olarak (Orta)

**Hedef:** Domain exception'larını anlamlı, makinece okunur problem yanıtlarına çevirmek.

`OrderController` ve `StockService` hazırdır. Stok yetersizse `OutOfStockException`, bilinmeyen bir ISBN için `UnknownBookException` fırlatılır. Şu an ikisi de `500` hatasıyla sonuçlanır.

**Yapılacaklar** (`exercise2.OrderProblemHandler`):

- `TODO 2a` — `OutOfStockException` → `409 Conflict`. Tip `https://springbootedu.com/problems/out-of-stock`, başlık `Out of stock`, açıklama exception mesajı, `instance` istek yolu. Ek alanlar: `isbn`, `requested`, `available`.
- `TODO 2b` — `UnknownBookException` → `404 Not Found`. Tip `https://springbootedu.com/problems/book-not-found`, başlık `Book not found`, `instance` istek yolu, ek alan `isbn`.

**İpuçları:**

- Ders bölüm 3.3: `ProblemDetail.forStatusAndDetail(...)`, `setType`, `setTitle`, `setInstance`, `setProperty`.
- İstek yolunu almak için metoda bir `HttpServletRequest` parametresi ekleyin.

**Kabul kriterleri:** `Exercise2Test` içindeki 3 testin hepsi geçer.

**Tahmini süre:** 30 dakika

# Ödev 3 — Sipariş Özetinin İki Versiyonu (Zor)

**Hedef:** Spring Framework 7 API versiyonlamasıyla, aynı URL'de uyumsuz iki yanıt biçimi sunmak.

`GET /api/order-summaries/{id}` bugün düz bir v1 yanıtı döner. Mobil uygulama; para birimini, okunur bir durum etiketini ve sipariş satırlarını istiyor. Mevcut istemciler bozulmamalı.

**Yapılacaklar:**

- `TODO 3a` — `application.yaml`: versiyonlamayı `API-Version` başlığıyla açın.
- `TODO 3b` — Desteklenen versiyonlar `1` ve `2`, başlıksız istekler `1` olsun.
- `TODO 3c` — Mevcut metodu versiyon `1` olarak işaretleyin.
- `TODO 3d` — Aynı URL için versiyon `2` metodunu ve `OrderSummaryV2` record'unu yazın:

```json
{"id": 1001,
 "total": {"amount": 145.00, "currency": "TRY"},
 "status": {"code": "PAID", "label": "Ödendi / Paid"},
 "lines": [{"title": "Effective Java", "quantity": 1}, {"title": "Java Puzzlers", "quantity": 1}]}
```

**İpuçları:**

- Ders bölüm 3.5: `spring.mvc.apiversion.*` ayarları ve `@GetMapping(path = "...", version = "2")`.
- İç içe record'lar JSON'da iç içe nesneler olur: `record Money(BigDecimal amount, String currency)`.
- Durum etiketi `OrderSummary.Status.label()` metodundan gelir.

**Kabul kriterleri:** `Exercise3Test` içindeki 4 testin hepsi geçer.

**Tahmini süre:** 45 dakika

# Ek Meydan Okuma (İsteğe Bağlı)

Yazar uç noktasını springdoc ile belgeleyin (`@Tag`, `@Operation`). `@WebMvcTest` yerine `@SpringBootTest(webEnvironment = RANDOM_PORT)` ve `RestTestClient` kullanan bir test yazıp `POST /api/authors` akışını gerçek bir sunucu üzerinde doğrulayın.
