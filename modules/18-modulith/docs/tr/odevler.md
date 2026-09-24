---
title: "Modül 18 — Spring Modulith"
subtitle: "Ödevler"
module: "18-modulith"
lang: tr-TR
date: "2026-09-25"
---

# Nasıl Çalışılır

1. Başlangıç kodu `modules/18-modulith/exercise/` altındadır. `TODO` yorumlarını bulun.
2. Ödev uygulamasında `catalog`, `review`, `order`, `inventory` ve `loyalty` modülleri vardır. Testler PostgreSQL'i Testcontainers ile başlatır, bu yüzden Docker çalışıyor olmalıdır.
3. Testleri çalıştırın:

```bash
./mvnw -Pexercises -pl modules/18-modulith/exercise -am test
```

4. Tüm testler yeşil olduğunda ödev tamamdır.
5. Takılırsanız önce ipuçlarını, sonra `modules/18-modulith/solution/` altındaki çözümü okuyun.

# Ödev 1 — Bir Sınır İhlalini Düzeltmek (Kolay)

**Hedef:** Review modülü, bir kitabın var olup olmadığını kataloğun repository'sini doğrudan kullanarak kontrol ediyor. Repository catalog modülünün içine aittir ve modül testi başarısız olur.

**Yapılacaklar** (`review.ReviewService`):

- `TODO 1` — `catalog.internal.BookRepository` yerine catalog modülünün API'sini kullanın.

**İpuçları:**

- Önce `Exercise1Test`'i çalıştırın ve mesajı okuyun: `Module 'review' depends on non-exposed type …BookRepository within module 'catalog'!`.
- Ders bölüm 3.1: API modül paketidir. `catalog.CatalogService`'in neler sunduğuna bakın.
- `Exercise1Test`, `verify()` yerine `detectViolations().throwIfPresent()` kullanır. Ders bölüm 4 nedenini açıklar.

**Kabul kriterleri:** `Exercise1Test` ve `Exercise1ReviewTest` geçer.

**Tahmini süre:** 10 dakika

# Ödev 2 — Yeni Bir Modül: Sadakat Puanları (Orta)

**Hedef:** Müşteriler bir sipariş toplamının her tam 10'u için bir sadakat puanı alır (179.80 → 17 puan). Sadakat modülü, order modülü onu bilmeden çalışmalıdır.

`loyalty.LoyaltyPoints` (modülün `pointsOf` içeren API'si ve modülün kendisi için `add`) hazır verilmiştir.

**Yapılacaklar** (`loyalty` paketi):

- `TODO 2a` — Order modülünün `OrderPlaced` event'lerini dinleyen bir sınıf oluşturun.
- `TODO 2b` — Puanları hesaplayın ve `add(...)` ile saklayın.
- `TODO 2c` — `package-info.java` içinde modülün yalnızca order modülüne bağımlı olabileceğini bildirin.

**İpuçları:**

- Ders bölüm 3.3: `@ApplicationModuleListener`. Listener sınıfı package-private olabilir.
- `total.divide(BigDecimal.TEN, 0, RoundingMode.DOWN).intValue()` aşağı yuvarlar.
- Ders bölüm 3.1: Pakette `@ApplicationModule(allowedDependencies = "order")`, `org.springframework.modulith`'ten import edilir.
- Test bir `@ApplicationModuleTest`'tir: Yalnızca loyalty modülü başlatılır ve `Scenario.publish(...)` event'i gönderir.

**Kabul kriterleri:** `Exercise2Test` içindeki 4 testin hepsi geçer.

**Tahmini süre:** 25 dakika

# Ödev 3 — Bir Çağrıdan Bir Event'e (Orta)

**Hedef:** `OrderService.place`, `inventory.reserve(...)`'i doğrudan çağırıyor. Bu yüzden order modülü inventory modülüne bağımlıdır ve onsuz test edilemez veya değiştirilemez. Çağrıyı bir event'e çevirin.

**Yapılacaklar:**

- `TODO 3a` (`order.OrderService`) — Çağrıyı ve `Inventory` alanını kaldırın. Bunun yerine bir `OrderPlaced` event'i yayınlayın (record hazır verilmiştir).
- `TODO 3b` (`inventory.StockReservations`) — Bir `OrderPlaced` event'i geldiğinde stoğu ayırın.
- `TODO 3c` (`inventory.Inventory`) — `reserve`'ü tekrar package-private yapın: Artık onu yalnızca inventory modülü çağırıyor.

**İpuçları:**

- Ders bölüm 3.2: `ApplicationEventPublisher`'ı enjekte edin ve `@Transactional` metodun içinde `publishEvent(...)`'i çağırın.
- Listener'ı ekleyip çağrıyı kaldırmayı unutursanız `Exercise1Test` bir döngü bildirir: `order → inventory → order`.
- `Exercise3OrderTest` yalnızca order modülünü başlatır. `OrderService` bir `Inventory` bean'ine ihtiyaç duyduğu sürece context başlayamaz.

**Kabul kriterleri:** `Exercise3Test`, `Exercise3OrderTest` ve `Exercise3InventoryTest` geçer ve `Exercise1Test` yeşil kalır.

**Tahmini süre:** 25 dakika

# Ek Meydan Okuma (İsteğe Bağlı)

Ödev projesine `spring-modulith-starter-jdbc` ekleyin, `event_publication` tablosunu bir Flyway migration'ıyla oluşturun (derste olduğu gibi) ve başarısız bir loyalty listener'ının `IncompleteEventPublications` ile yeniden gönderildiğini gösteren bir test yazın.
