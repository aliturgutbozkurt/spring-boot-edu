---
title: "Modül {{MODULE_NO}} — {{TITLE_TR}}"
subtitle: "Ödevler"
module: "{{MODULE_ID}}"
lang: tr-TR
date: "{{DATE}}"
---

# Nasıl Çalışılır?

1. Başlangıç kodu `modules/{{MODULE_ID}}/exercise/` içindedir. `TODO` yorumlarını bulun.
2. Her ödevin testleri hazırdır ve siz çözene kadar **kırmızıdır**.
3. Testleri çalıştırın:

```bash
./mvnw -Pexercises -pl modules/{{MODULE_ID}}/exercise test
```

4. Tüm testler yeşil olduğunda ödev tamamdır.
5. Takıldığınızda önce ipuçlarına, sonra `modules/{{MODULE_ID}}/solution/` içindeki çözüme bakın.

> [!TIP]
> Tek bir ödevin testini çalıştırmak için: `-Dtest=Exercise1Test`

# Ödev 1 — ... (Kolay)

**Hedef:** ...

**Yapılacaklar:**

- `exercise/src/main/java/com/springbootedu/{{PACKAGE}}/...` içindeki `TODO 1` ...

**İpuçları:**

- Ders bölüm 3.1'e bakın.

**Kabul kriterleri:** `Exercise1Test` içindeki tüm testler geçer.

**Tahmini süre:** 20 dakika

# Ödev 2 — ... (Orta)

**Hedef:** ...

**Yapılacaklar:**

- ...

**İpuçları:**

- ...

**Kabul kriterleri:** `Exercise2Test` içindeki tüm testler geçer.

**Tahmini süre:** 40 dakika

# Ödev 3 — ... (Zor)

**Hedef:** ...

**Yapılacaklar:**

- ...

**İpuçları:**

- ...

**Kabul kriterleri:** `Exercise3Test` içindeki tüm testler geçer.

**Tahmini süre:** 60 dakika

# Ek Meydan Okuma (İsteğe Bağlı)

Testi olmayan, açık uçlu bir genişletme fikri.
