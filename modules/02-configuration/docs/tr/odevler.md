---
title: "Modül 02 — Konfigürasyon ve Auto-Configuration"
subtitle: "Ödevler"
module: "02-configuration"
lang: tr-TR
date: "2026-09-23"
---

# Nasıl Çalışılır?

1. Başlangıç kodu `modules/02-configuration/exercise/` içindedir. `TODO` yorumlarını bulun (Java dosyaları, `application.yaml` ve `META-INF/spring/...imports`).
2. Her ödevin testleri hazırdır ve siz çözene kadar **kırmızıdır**.
3. Testleri çalıştırın:

```bash
./mvnw -Pexercises -pl modules/02-configuration/exercise -am test
```

4. Tüm testler yeşil olduğunda ödev tamamdır.
5. Takıldığınızda önce ipuçlarına, sonra `modules/02-configuration/solution/` içindeki çözüme bakın.

> [!TIP]
> Tek bir ödevin testini çalıştırmak için: `-Dtest=Exercise1Test -Dsurefire.failIfNoSpecifiedTests=false`

# Ödev 1 — Doğrulanan Çalışma Saatleri (Kolay)

**Hedef:** Tipli ve doğrulanan bir `@ConfigurationProperties` record'u yazmak.

Kitapçının çalışma saatleri `bookstore.opening-hours.*` altında tanımlıdır. Geçersiz bir ayar (ör. kapanış saati açılıştan önce) uygulamanın başlamasını engellemelidir.

**Yapılacaklar** (`exercise1.OpeningHoursProperties`):

- `TODO 1a` — Record'u başlangıçta doğrulanacak şekilde işaretleyin.
- `TODO 1b` — `opens` ve `closes` zorunlu olsun.
- `TODO 1c` — `maxReservations` 1 ile 10 arasında olsun ve verilmezse 3 olsun.
- `TODO 1d` — `closedDays` verilmezse boş liste olsun.
- `TODO 1e` — Kapanış saati açılıştan sonra değilse doğrulama başarısız olsun.
- `TODO 1f` — `isOpen(day, time)`: kapalı olmayan günlerde, açılış dahil ve kapanış hariç `true` döndürsün.

**İpuçları:**

- Ders bölüm 3.2: `@Validated`, `@NotNull`, `@Min`/`@Max`, `@DefaultValue`.
- Alanlar arası kural için `isXxx()` adlı bir `boolean` metoda `@AssertTrue(message = "...")` ekleyin. Değerler `null` olabilir, önce onu kontrol edin.

**Kabul kriterleri:** `Exercise1Test` içindeki 5 testin hepsi geçer.

**Tahmini süre:** 30 dakika

# Ödev 2 — Profillere Göre Fiyatlandırma (Orta)

**Hedef:** Aynı kodla, profil dosyaları ve bir profil grubuyla farklı fiyatlar üretmek.

`PricingProperties` (`bookstore.pricing.*`) hazırdır. Değerleri YAML dosyalarına siz yazacaksınız.

**Yapılacaklar** (`exercise/src/main/resources/`):

- `TODO 2a` — `application.yaml`: varsayılan kargo ücreti `29.90`, indirim `0`.
- `TODO 2b` — `application-campaign.yaml`: kargo `0.00`, indirim `20`. `application-express.yaml`: kargo `49.90`.
- `TODO 2c` — `black-friday` adında, önce `campaign`, sonra `express` profilini açan bir profil grubu.

**İpuçları:**

- Ders bölüm 3.4: `spring.profiles.group.<ad>: profil1, profil2`.
- Bir grupta **sonra gelen** profil aynı anahtar için kazanır. Bu yüzden black-friday'de indirim campaign'den, kargo ücreti express'ten gelir.
- Para değerlerini tırnak içinde yazın: `"29.90"`.

**Kabul kriterleri:** `Exercise2Test` içindeki 4 testin hepsi geçer.

**Tahmini süre:** 30 dakika

# Ödev 3 — Kendi Auto-Configuration'ınız (Zor)

**Hedef:** Koşullarla korunan, kullanıcı bean'i varsa geri çekilen ve imports dosyasıyla bulunan bir auto-configuration yazmak.

`ExchangeRateService`, `FixedExchangeRateService` ve `ExchangeRateProperties` (`bookstore.exchange.*`) hazırdır. Eksik olan, onları bir araya getiren auto-configuration'dır.

**Yapılacaklar** (`exercise3.ExchangeRateAutoConfiguration` ve imports dosyası):

- `TODO 3a` — Sınıfı bir auto-configuration yapın.
- `TODO 3b` — Yalnızca `bookstore.exchange.enabled` `true` ise veya hiç tanımlı değilse çalışsın.
- `TODO 3c` — `ExchangeRateProperties`'i bağlayın.
- `TODO 3d` — Ayarlardaki kurlarla bir `FixedExchangeRateService` bean'i oluşturun. Uygulamanın kendi `ExchangeRateService`'i varsa oluşturmayın.
- `TODO 3e` — Sınıfı `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` dosyasına kaydedin.

**İpuçları:**

- Ders bölüm 3.6'daki `GreetingAutoConfiguration` birebir örnektir.
- `@ConditionalOnBooleanProperty(name = ..., matchIfMissing = true)`, `@EnableConfigurationProperties`, `@ConditionalOnMissingBean`.
- Imports dosyasına sınıfın **tam adını** (paket dahil) yazın.

**Kabul kriterleri:** `Exercise3Test` içindeki 5 testin hepsi geçer.

**Tahmini süre:** 45 dakika

# Ek Meydan Okuma (İsteğe Bağlı)

Ödev 3'ü ders bölüm 3.6'daki gibi iki ayrı Maven modülüne (`...-autoconfigure` ve `...-spring-boot-starter`) bölün ve starter'ı ders uygulamasında kullanın. `bookstore.exchange.rates.USD` ayarı için IDE'nizin otomatik tamamlama önerdiğini kontrol edin.
