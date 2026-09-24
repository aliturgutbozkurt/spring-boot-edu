---
title: "Modül 14 — Spring Boot'ta Test"
subtitle: "Ödevler"
module: "14-testing"
lang: tr-TR
date: "2026-09-24"
---

# Nasıl Çalışılır

Bu modülde **testleri siz yazarsınız**. Test edilen kod hazır verilmiştir ve değiştirilmemelidir.

1. Başlangıç kodu `modules/14-testing/exercise/` altındadır. `TODO` yorumları `src/test/` içindedir.
2. Ödevler Docker istemez.
3. Testleri çalıştırın:

```bash
./mvnw -Pexercises -pl modules/14-testing/exercise -am test
```

4. Tüm testler yeşil olduğunda ödev tamamdır. Buna ödevlerle birlikte gelen kontroller de (`Exercise1MutantsTest`, `Exercise3RulesCatchLegacyTest`) dahildir. Bunlar testlerinizin hataları gerçekten yakalayacağından emin olur.
5. Takılırsanız önce ipuçlarını, sonra `modules/14-testing/solution/src/test/` altındaki çözümü okuyun.

# Ödev 1 — Test Edilmemiş Bir Servisi Test Etmek (Kolay)

**Hedef:** `LoyaltyCalculator` sadakat puanlarını hesaplar. Henüz hiç testi yoktur. Javadoc'u beş kural anlatır. Bunlardan herhangi birindeki bir hatayı yakalayacak testler yazın.

**Yapılacaklar** (`src/test` içinde `exercise1.LoyaltyCalculatorTest`):

- `TODO 1a` — Minimum sipariş (19.99 ve tam 20.00) ve "yalnızca tam 10.00'lar sayılır" (29.99).
- `TODO 1b` — Aynı sipariş BRONZE, SILVER ve GOLD için. SILVER'daki yuvarlamaya dikkat edin.
- `TODO 1c` — Doğum günü bonusu.
- `TODO 1d` — 500 puanlık üst sınır.

**İpuçları:**

- Ders bölüm 3.1: `@CsvSource`'lu bir `@ParameterizedTest` birçok durumu az satırla test eder.
- Hesaplayıcıyı her zaman `Subjects.loyaltyCalculator()` ile oluşturun. `Exercise1MutantsTest` onu **bilerek hatalı** üç hesaplayıcıyla değiştirir ve testlerinizin her birine karşı başarısız olmasını bekler. Bir mutant hayatta kalırsa bir kural yeterince iyi test edilmemiştir.
- En çok hatayı sınırlar bulur: tam sınırda ve bir adım altında test edin.

**Kabul kriterleri:** `LoyaltyCalculatorTest` geçer ve `Exercise1MutantsTest` geçer (üç mutantın hepsi yakalanır).

**Tahmini süre:** 30 dakika

# Ödev 2 — Kararsız (Flaky) Testleri Düzeltmek (Orta)

**Hedef:** `DeliveryEstimatorTest`, bazı günlerde, çalıştırmalarda veya bilgisayarlarda geçen, diğerlerinde başarısız olan üç test içerir. Her birini **üretim kodunu değiştirmeden** deterministik yapın.

**Yapılacaklar** (`exercise2.DeliveryEstimatorTest`):

- `TODO 2a` — Test bugünün tarihine bağlıdır. Önümüzdeki üç güne bir hafta sonu düştüğünde başarısız olur.
- `TODO 2b` — Test, sırası garanti edilmeyen bir `HashSet`'in dolaşma sırasına bağlıdır.
- `TODO 2c` — Test sabit 200 ms bekler, ama gönderim 50–400 ms sürer.

**İpuçları:**

- `DeliveryEstimator` bir `Clock` alır: `Clock.fixed(Instant.parse("2026-09-21T10:00:00Z"), ZoneOffset.UTC)` her zaman aynı pazartesidir. Sonra bir hafta içi günü ve hafta sonunu kapsayan birer test yazın.
- AssertJ: `containsExactlyInAnyOrder(...)`.
- Awaitility: `await().atMost(Duration.ofSeconds(2)).until(() -> …)` yalnızca gerektiği kadar bekler.
- İyi bir kontrol: Test sınıfını arka arkaya on kez çalıştırın (IntelliJ: *Run until failure*).

**Kabul kriterleri:** `DeliveryEstimatorTest` içindeki 3 testin hepsi her seferinde geçer.

**Tahmini süre:** 30 dakika

# Ödev 3 — Mimari Kuralları Yazmak (Zor)

**Hedef:** `exercise3.shop` paketi katmanlı kurulmuştur: `web → service → repository`. `exercise3.legacy` paketi bu kuralları çiğner. Mağaza için geçen ve eski (legacy) kodu yakalayacak ArchUnit kuralları yazın.

**Yapılacaklar** (`exercise3.ShopArchitectureTest`):

- `TODO 3a` — Bir `repository` paketindeki sınıfları yalnızca `service` (ve `repository`) paketleri kullanabilir.
- `TODO 3b` — Adı `Controller` ile biten sınıflar `repository` paketine bağımlı olmamalıdır.
- `TODO 3c` — `…Repository` veya `…Store` adlı sınıflar bir `repository` paketinde durmalıdır.

**İpuçları:**

- Ders bölüm 3.8 stili gösterir: `noClasses().that()…should()…` ve `classes().that()…should()…`.
- 3a: `.should().onlyBeAccessed().byAnyPackage("..service..", "..repository..")`.
- 3c: `.that().haveSimpleNameEndingWith("Repository").or().haveSimpleNameEndingWith("Store")`.
- `ShopArchitectureTest` yalnızca `shop` paketini kontrol eder. `Exercise3RulesCatchLegacyTest` kurallarınızı `legacy` dahil tüm ödeve uygular ve her kuralın bir ihlal bulmasını bekler.

**Kabul kriterleri:** `ShopArchitectureTest` ve `Exercise3RulesCatchLegacyTest` geçer.

**Tahmini süre:** 40 dakika

# Ek Meydan Okuma (İsteğe Bağlı)

`solution/src/test/…/exercise1/mutants` altındaki üç mutanta bakın. `LoyaltyCalculator`'da testlerinizin **yakalamayacağı** dördüncü bir hata uydurun, onu mutant olarak ekleyin ve ardından onu yakalayan testi ekleyin. PIT (pitest.org) gibi araçlar bunu tüm kod tabanı için otomatik yapar.
