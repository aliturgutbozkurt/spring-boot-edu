---
title: "Modül 05 — Spring JDBC ve PostgreSQL"
subtitle: "Ödevler"
module: "05-data-jdbc-postgres"
lang: tr-TR
date: "2026-09-23"
---

# Nasıl Çalışılır?

1. Başlangıç kodu `modules/05-data-jdbc-postgres/exercise/` içindedir. `TODO` yorumlarını ve `db/migration/TODO-exercise-1.txt` dosyasını bulun.
2. Testler gerçek bir PostgreSQL ile çalışır (Testcontainers). **Docker açık olmalıdır.**
3. Testleri çalıştırın:

```bash
./mvnw -Pexercises -pl modules/05-data-jdbc-postgres/exercise -am test
```

4. Tüm testler yeşil olduğunda ödev tamamdır.
5. Takıldığınızda önce ipuçlarına, sonra `modules/05-data-jdbc-postgres/solution/` içindeki çözüme bakın.

> [!IMPORTANT]
> Ödevler sırayla yapılmalıdır: Ödev 2 ve 3, Ödev 1'de oluşturacağınız `review` tablosunu kullanır.

# Ödev 1 — Yorumlar İçin Migration (Kolay)

**Hedef:** Bir tabloyu, kurallarını veritabanında koruyacak şekilde bir Flyway migration'ı ile eklemek.

**Yapılacaklar:** `exercise/src/main/resources/db/migration/V3__create_review.sql` dosyasını oluşturun. `review` tablosu:

| Sütun | Kural |
|---|---|
| `id` | `bigserial`, birincil anahtar |
| `book_id` | Boş olamaz, `book(id)`'ye başvurur. Kitap silinince yorumları da silinir |
| `stars` | `smallint`, boş olamaz, yalnızca 1–5 |
| `comment` | `text` |
| `created_at` | `timestamptz`, boş olamaz, varsayılanı şimdiki zaman |

**İpuçları:**

- Ders bölüm 3.2'deki `V1__create_schema.sql` örnek alınabilir.
- `references book (id) on delete cascade`, `check (stars between 1 and 5)`, `default now()`.
- Dosya adında **iki** alt çizgi vardır: `V3__create_review.sql`.

**Kabul kriterleri:** `Exercise1Test` içindeki 6 testin hepsi geçer.

**Tahmini süre:** 20 dakika

# Ödev 2 — Yorum Repository'si ve Toplu Ekleme (Orta)

**Hedef:** `JdbcClient` ile CRUD yazmak ve çok sayıda satırı tek bir batch ile eklemek.

**Yapılacaklar** (`exercise2.ReviewRepository`):

- `TODO 2a` — `add(review)`: yorumu ekleyip üretilen `id`'yi döndürün. Kitap ISBN ile verilir.
- `TODO 2b` — `findByIsbn(isbn)`: kitabın yorumları, en yenisi önce, `Review` record'larına eşlenmiş olarak.
- `TODO 2c` — `averageStars(isbn)`: ortalama puan. Hiç yorum yoksa boş `Optional`.
- `TODO 2d` — `addAll(reviews)`: tüm yorumları **tek bir batch** ile ekleyin ve güncelleme sayılarını döndürün.

**İpuçları:**

- Kitap id'si INSERT içinde bulunabilir: `(select id from book where isbn = :isbn)`.
- `.paramSource(record)`, parametreleri record bileşenlerinden alır. Üretilen anahtar için `KeyHolder` kullanın (ders bölüm 3.3).
- `avg()` PostgreSQL'de `numeric` döner ve hiç satır yoksa `NULL` olur. `::float8` ile dönüştürün ve `NULL`'ı `Optional` ile karşılayın.
- `JdbcClient`'ın batch API'si yoktur. `NamedParameterJdbcTemplate.batchUpdate(sql, SqlParameterSourceUtils.createBatch(list))` kullanın.

**Kabul kriterleri:** `Exercise2Test` içindeki 3 testin hepsi geçer.

**Tahmini süre:** 40 dakika

# Ödev 3 — Hepsi ya da Hiçbiri: Yorum İçe Aktarma (Zor)

**Hedef:** Bir içe aktarma işlemini tek bir transaction yapmak ve sonucunu, işlem geri alınsa bile kaydetmek.

**Yapılacaklar** (`exercise3` paketi):

- `TODO 3a` — `ReviewImportService.importAll`: tüm yorumlar kaydedilsin ya da hiçbiri kaydedilmesin.
- `TODO 3b` — Başarıda `ImportLog`'a `SUCCESS` ve `"<n> reviews"` yazın. Hatada `FAILED` ve exception'ın basit sınıf adını yazın, sonra hatayı tekrar fırlatın.
- `TODO 3c` — `ImportLog.record`: bu kayıt, çağıranın transaction'ı geri alınsa bile kalıcı olmalı.

**İpuçları:**

- Ders bölüm 3.5: `@Transactional` ve `@Transactional(propagation = Propagation.REQUIRES_NEW)`.
- Geçersiz bir puan (ör. 9) veritabanı kısıtına takılır ve bir `RuntimeException` olarak gelir.
- Testler `@SpringBootTest` kullanır ve gerçekten commit eder. Veriyi kendileri temizler.

**Kabul kriterleri:** `Exercise3Test` içindeki 2 testin ikisi de geçer.

**Tahmini süre:** 40 dakika

# Ek Meydan Okuma (İsteğe Bağlı)

Yorumları Spring Data JDBC ile modelleyin: `Book` aggregate'i yorumlarını bir `@MappedCollection` olarak içersin. Bir kitabı yorumlarıyla birlikte kaydeden ve okuyan bir `@DataJdbcTest` yazın. `JdbcClient` sürümüyle karşılaştırın: hangisi hangi durumda daha uygun?
