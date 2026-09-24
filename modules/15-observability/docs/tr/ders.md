---
title: "Modül 15 — Gözlemlenebilirlik: Metrikler, Trace'ler, Loglar"
subtitle: "Ders Notları"
module: "15-observability"
lang: tr-TR
date: "2026-09-24"
---

<!--
  YAZAR NOTLARI (yayından önce silin)
  - Bölüm numaraları EN dokümanla birebir aynı kalmalı (check-module.sh başlık sayısını karşılaştırır).
  - Koddaki "// Ders 3.2 / Lesson 3.2" yorumları bu dokümandaki 3.2 başlığını gösterir.
  - Kod parçaları derlenen kaynaktan kopyalanır. Her kod bloğunun hemen üstüne kaynağını bir HTML yorumu olarak yazın
    Kaynakta bölgeyi // tag::etiket-adi[] ... // end::etiket-adi[] ile işaretleyin, ./scripts/sync-snippets.sh kodu kopyalar.
    (içerik: snippet: lesson/src/main/java/com/springbootedu/<paket>/Dosya.java#etiket-adi) — aşağıdaki 3.1 örneğine bakın.
    check-module.sh bu satırları kaynakla karşılaştırır.
  - Not kutuları: > [!NOTE], > [!TIP], > [!IMPORTANT], > [!WARNING], > [!CAUTION]
-->

# 1. Öğrenme Hedefleri

Bu modülün sonunda şunları yapabileceksiniz:

- ...
- ...
- ...

**Ön koşullar:** Modül ... · **Tahmini süre:** ... saat

# 2. Kavramlar

## 2.1 ...

Kavramı kısa ve somut anlatın; gerekirse diyagram ekleyin (`../assets/diyagram.svg`).

> [!NOTE]
> Bir kavramı netleştiren kısa not.

# 3. Adım Adım Örnekler

Modülü çalıştırmak için:

```bash
./mvnw -pl modules/15-observability/lesson spring-boot:run
```

## 3.1 ...

**Amaç:** Bu örnek neyi gösteriyor?

<!-- snippet: lesson/src/main/java/com/springbootedu/observability/ObservabilityApplication.java#application -->
```java
@SpringBootApplication
public class ObservabilityApplication {

    public static void main(String[] args) {
        SpringApplication.run(ObservabilityApplication.class, args);
    }
}
```

**Çalıştırın:**

```bash
curl -s localhost:8080/api/...
```

**Beklenen çıktı:**

```json
{ }
```

**Testi:** `lesson/src/test/java/com/springbootedu/observability/OrnekTest.java`

# 4. Sık Yapılan Hatalar ve En İyi Pratikler

> [!WARNING]
> Sık yapılan bir hata ve neden sorun olduğu.

- **Yapın:** ...
- **Yapmayın:** ...

# 5. Özet

- ...
- ...

Sıradaki adım: [Ödevler](odevler.md)

# 6. İleri Okuma

- [Spring Boot Referans Dokümanı](https://docs.spring.io/spring-boot/reference/)
- ...
