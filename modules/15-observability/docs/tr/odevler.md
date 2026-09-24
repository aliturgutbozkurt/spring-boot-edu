---
title: "Modül 15 — Gözlemlenebilirlik: Metrikler, Trace'ler, Loglar"
subtitle: "Ödevler"
module: "15-observability"
lang: tr-TR
date: "2026-09-24"
---

# Nasıl Çalışılır

1. Başlangıç kodu `modules/15-observability/exercise/` altındadır. `TODO` yorumlarını bulun.
2. Ödevler Docker istemez: Testler bellek içi registry'ler kullanır.
3. Testleri çalıştırın:

```bash
./mvnw -Pexercises -pl modules/15-observability/exercise -am test
```

4. Tüm testler yeşil olduğunda ödev tamamdır.
5. Takılırsanız önce ipuçlarını, sonra `modules/15-observability/solution/` altındaki çözümü okuyun.

# Ödev 1 — Ödeme Metrikleri (Kolay)

**Hedef:** İş birimi, ödeme yöntemi başına kaç ödeme yapıldığını ve ne kadar para hareket ettiğini görmek istiyor.

**Yapılacaklar** (`exercise1.CheckoutMetrics`):

- `TODO 1a` — Temel birimi `TRY` olan bir `bookstore.checkout.amount` distribution summary'si.
- `TODO 1b` — Her ödemeyi `payment=<yöntem>` tag'iyle `bookstore.checkouts` içinde sayın.
- `TODO 1c` — Yalnızca `card` ve `transfer` gerçek tag değerleridir. Geri kalan her şey `other` olarak sayılır.
- `TODO 1d` — Tutarı summary'ye kaydedin.

**İpuçları:**

- Ders bölüm 3.2: `Counter.builder(name).tag(key, value).register(registry).increment()`.
- `DistributionSummary.builder(name).baseUnit("TRY").register(registry)`, ardından `record(amount.doubleValue())`.
- Neden `other`? Ödeme yöntemi client'tan gelir. Sınır olmadan her yazım hatası yeni bir zaman serisi oluştururdu (ders bölüm 4).

**Kabul kriterleri:** `Exercise1Test` içindeki 3 testin hepsi geçer.

**Tahmini süre:** 20 dakika

# Ödev 2 — Yavaş Adımı Bulmak (Orta)

**Hedef:** Aylık rapor çok uzun sürüyor ama hangi adımın yavaş olduğunu kimse bilmiyor. Bir trace yanıtı göstersin diye onu enstrümante edin.

**Yapılacaklar** (`exercise2.ReportService.build`):

- `TODO 2a` — Tüm raporu `report.build` olarak gözlemleyin.
- `TODO 2b` — Her adımı bir **alt** (child) observation olarak gözlemleyin: `report.load-orders`, `report.load-customers`, `report.render`.

**İpuçları:**

- `Observation.createNotStarted(name, registry).observe(() -> …)` bir observation'ı başlatır, çalıştırır ve durdurur. İçinde observation "geçerli" olandır.
- Başka biri geçerliyken başlatılan bir observation otomatik olarak onun alt observation'ı, bir trace'te ise alt span'i olur.
- `observe(Supplier)` supplier'ın değerini döndürür. Böylece adımlar sonuçlarını aktarabilir.
- İkinci test observation'ları timer'lara çevirir ve hangi adımın en yavaş olduğunu kontrol eder. Sonra dersi Grafana ile başlatın ve aynı şeyi görmek için bir trace'e bakın.

**Kabul kriterleri:** `Exercise2Test` içindeki iki test de geçer.

**Tahmini süre:** 30 dakika

# Ödev 3 — Ödeme Sağlayıcısı için Health Kontrolü (Orta)

**Hedef:** Ödeme sağlayıcısı yavaş, kapalı veya bakımdayken uygulama bunu `/actuator/health` içinde söylemelidir.

| Sağlayıcının yanıtı | Durum | Ayrıntılar |
|---|---|---|
| bakımda | `OUT_OF_SERVICE` | — |
| 500 ms'den yavaş | `DOWN` | `reason`, `responseTimeMs` |
| diğer durumlar | `UP` | `responseTimeMs` |
| `ping()` exception fırlatır | `DOWN` | `error` |

**Yapılacaklar** (`exercise3.PaymentProviderHealthIndicator.health`):

- `TODO 3a` — Sağlayıcıya ping atın. Bakım → `OUT_OF_SERVICE`.
- `TODO 3b` — 500 ms'den yavaş → `reason = "slower than 500 ms"` ayrıntısıyla `DOWN`.
- `TODO 3c` — Aksi hâlde `responseTimeMs` ayrıntısıyla `UP`.
- `TODO 3d` — Bir exception → hatayla birlikte `DOWN`.

**İpuçları:**

- Ders bölüm 3.1: `Health.up().withDetail(...)`, `Health.down()`, `Health.outOfService()`.
- `Health.down(exception)`, exception'ı `error` ayrıntısı olarak ekler.
- `paymentProviderHealthIndicator` bean adı bileşeni `paymentProvider` yapar (`Exercise3ApplicationTest`).

**Kabul kriterleri:** `Exercise3Test` ve `Exercise3ApplicationTest` içindeki tüm testler geçer.

**Tahmini süre:** 25 dakika

# Ek Meydan Okuma (İsteğe Bağlı)

`bookstore.checkouts` counter'ını dersin Grafana dashboard'una (`grafana/bookstore-orders-dashboard.json`) ödeme yöntemine göre bölünmüş bir panel olarak ekleyin. Ardından 10 dakika boyunca hiç ödeme olmadığında tetiklenen bir Grafana alarm kuralı yazın.
