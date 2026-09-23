---
title: "Modül 01 — Spring Core Container: IoC ve Bağımlılık Enjeksiyonu"
subtitle: "Ödevler"
module: "01-core-container"
lang: tr-TR
date: "2026-09-23"
---

# Nasıl Çalışılır?

1. Başlangıç kodu `modules/01-core-container/exercise/` içindedir. `TODO` yorumlarını bulun.
2. Her ödevin testleri hazırdır ve siz çözene kadar **kırmızıdır**.
3. Testleri çalıştırın:

```bash
./mvnw -Pexercises -pl modules/01-core-container/exercise test
```

4. Tüm testler yeşil olduğunda ödev tamamdır.
5. Takıldığınızda önce ipuçlarına, sonra `modules/01-core-container/solution/` içindeki çözüme bakın.

> [!TIP]
> Tek bir ödevin testini çalıştırmak için: `-Dtest=Exercise1Test`

# Ödev 1 — Kargo Stratejileri (Kolay)

**Hedef:** Aynı arayüzün birden fazla uygulamasını bir `Map` olarak enjekte edip strateji deseni kurmak.

Kitapçı üç kargo yöntemi sunuyor. Her yöntem bir `ShippingCalculator` bean'idir ve **bean adı yöntemin adıdır** (`standard`, `express`). Spring, `Map<String, ShippingCalculator>` tipindeki bir constructor parametresine tüm bu bean'leri, adlarını anahtar yaparak verir.

**Yapılacaklar** (`exercise1` paketi):

- `TODO 1a` — `StorePickupShipping` sınıfını `pickup` adıyla bean yapın ve ücreti `0.00` döndürün.
- `TODO 1b` — `ShippingService.availableMethods()`: yöntem adlarını alfabetik sırayla döndürün.
- `TODO 1c` — `ShippingService.cost(...)`: yönteme ait hesaplayıcıyı bulun. Bilinmeyen bir yöntem için mesajında yöntemin adı ve geçerli yöntemlerin listesi bulunan bir `IllegalArgumentException` fırlatın.

**İpuçları:**

- `StandardShipping` sınıfına bakın: bean adı `@Component("...")` ile verilir.
- Ders bölüm 3.6'daki `List<NotificationChannel>` enjeksiyonunu hatırlayın. `Map` de aynı şekilde çalışır.

**Kabul kriterleri:** `Exercise1Test` içindeki 4 testin hepsi geçer.

**Tahmini süre:** 20 dakika

# Ödev 2 — Hediye Paketi Özellik Bayrağı (Orta)

**Hedef:** Bir özelliği (feature flag) konfigürasyonla açıp kapatmak ve her durumda **tam olarak bir** bean oluşmasını sağlamak.

`bookstore.gift-wrap.enabled=true` ise ücretli hediye paketi (`PaidGiftWrap`) sunulmalı. Değer `false` ise **ya da hiç tanımlı değilse** `NoGiftWrap` kullanılmalı. Ücret `bookstore.gift-wrap.price` ile değiştirilebilmeli ve varsayılanı `15.00` olmalı.

**Yapılacaklar** (`exercise2.GiftWrapConfiguration`):

- `TODO 2a` — Bayrak `true` iken `PaidGiftWrap` döndüren bir `@Bean` metodu ekleyin.
- `TODO 2b` — Ücreti property'den alın, property yoksa `15.00` kullanın.
- `TODO 2c` — Bayrak `false` iken **veya eksikken** `NoGiftWrap` döndüren bir `@Bean` metodu ekleyin.

**İpuçları:**

- Ders bölüm 3.5: `@ConditionalOnProperty(name = ..., havingValue = ..., matchIfMissing = ...)`.
- Varsayılan değerli property: `@Value("${anahtar:varsayılan}")` bir `@Bean` metodunun parametresine de yazılabilir.

**Kabul kriterleri:** `Exercise2Test` içindeki 4 testin hepsi geçer.

**Tahmini süre:** 30 dakika

# Ödev 3 — Takip Edilen Yazar Bildirimi (Zor)

**Hedef:** Bir olay yayınlamak, onu koşullu bir dinleyiciyle işlemek ve dinleyicilerin sırasını belirlemek.

Yeni bir kitap eklendiğinde `BookPublisher` bir `BookAddedEvent` yayınlamalı. Müşteri kitabın yazarını takip ediyorsa `FollowerNotifier` gelen kutusuna (`Inbox`) bir bildirim yazmalı. Hazır verilen `AuditTrail` dinleyicisi her kitabı `@Order(10)` ile kaydeder. Bildirim, audit kaydından **önce** yazılmalıdır.

**Yapılacaklar** (`exercise3` paketi):

- `TODO 3a` — `BookPublisher` constructor'ından bir `ApplicationEventPublisher` alın.
- `TODO 3b` — `publish(...)` içinde bir `BookAddedEvent` yayınlayın.
- `TODO 3c` — `FollowerNotifier.onBookAdded` metodunu bir olay dinleyicisi yapın.
- `TODO 3d` — Dinleyici yalnızca yazar takip ediliyorsa çalışsın.
- `TODO 3e` — Dinleyici `AuditTrail`'den önce çalışsın.
- `TODO 3f` — Gelen kutusuna tam olarak şu mesajı yazın: `Takip ettiğiniz yazar / Followed author: <yazar> — <başlık>`

**İpuçları:**

- Ders bölüm 3.7: `@EventListener` ve `@Order`.
- Koşulu metodun içinde bir `if` ile de yazabilirsiniz. Daha zarif çözüm, SpEL ile bir bean'e başvurmaktır: `condition = "@followedAuthors.isFollowed(#event.author())"`.

**Kabul kriterleri:** `Exercise3Test` içindeki 3 testin hepsi geçer.

**Tahmini süre:** 45 dakika

# Ek Meydan Okuma (İsteğe Bağlı)

Ödev 1'deki kargo yöntemlerini `BeanRegistrar` ile, `bookstore.shipping.methods=standard,express` gibi bir property'den okuyarak kaydedin (ders bölüm 3.6). Konfigürasyonda olmayan yöntemler bean olmamalı. Kendi testinizi de yazın.
