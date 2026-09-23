---
title: "Modül 99 — PDF Hattı Denemesi"
subtitle: "Ders Notları"
module: "99-sample"
lang: tr-TR
date: "2026-09-23"
---

# Öğrenme Hedefleri

Bu doküman PDF hattını doğrular: **ç ğ ı İ ö ş ü Ç Ğ Ö Ş Ü** — "Işık, çiğ köfte, şöyle güzel!"

- Türkçe karakterler ve heceleme
- Kod blokları ve renklendirme
- Not/uyarı kutuları, tablo ve görsel

# Kavramlar

`@RestController` bir sınıfı REST uç noktası yapar.[^1]

[^1]: Dipnot: ayrıntılar resmî dokümanda.

| Kavram | Açıklama |
|---|---|
| Bean | Spring'in yönettiği nesne |
| Bağımlılık Enjeksiyonu | Nesnelerin dışarıdan verilmesi |

> [!NOTE]
> Bu bir **not** kutusudur. Birden fazla satır
> içerebilir.

> [!TIP]
> İpucu: `./mvnw spring-boot:run` ile çalıştırın.

> [!WARNING]
> Uyarı: field injection kullanmayın.

> [!IMPORTANT]
> Önemli bilgi.

> [!CAUTION]
> Dikkat: `docker compose down -v` veriyi siler.

> Bu normal bir alıntıdır, kutuya dönüşmemeli.

# Adım Adım Örnek

```java
@RestController
@RequestMapping("/api/books")
class BookController {                       // Ders 3.1 — minimal REST controller

    private final BookService books;

    BookController(BookService books) {      // constructor injection
        this.books = books;
    }

    @GetMapping("/{id}")
    BookResponse find(@PathVariable long id) {
        return books.find(id);               // BookNotFoundException → ProblemDetail (404)
    }
}
```

```yaml
spring:
  application:
    name: kitapçı
```

```bash
./mvnw -pl modules/03-web-mvc/lesson spring-boot:run
```

![Kurs logosu](../../../assets/logo.svg){width=20%}

# Özet

Hepsi bu kadar.
