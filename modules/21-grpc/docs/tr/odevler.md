---
title: "Modül 21 — gRPC"
subtitle: "Ödevler"
module: "21-grpc"
lang: tr-TR
date: "2026-09-25"
---

# Nasıl Çalışılır

1. Başlangıç kodu `modules/21-grpc/exercise/` altındadır. `TODO` yorumlarını bulun (`src/main/proto/book_service.proto` içinde de).
2. Ödevler Docker istemez: Testler in-process taşımayı kullanır.
3. Testleri çalıştırın:

```bash
./mvnw -Pexercises -pl modules/21-grpc/exercise -am test
```

4. Tüm testler yeşil olduğunda ödev tamamdır.
5. Takılırsanız önce ipuçlarını, sonra `modules/21-grpc/solution/` altındaki çözümü okuyun.

# Ödev 1 — Geriye Uyumlu Bir Şema Değişikliği (Orta)

**Hedef:** Kitap bir yayın yılı alıyor ve yeni bir `SearchBooks` RPC'si tanımlandı. Hâlâ eski mesajı (`old_book.proto`) kullanan istemciler çalışmaya devam etmelidir.

**Yapılacaklar:**

- `TODO 1a` (`book_service.proto`) — Eski istemcileri bozmadan `Book`'a `year` (`int32`) alanını ekleyin.
- `TODO 1b` (`exercise1.BookServiceImpl`) — `SearchBooks`'u gerçekleştirin: Başlığı büyük/küçük harf fark etmeden `title_contains`'i içeren tüm kitaplar, tek bir yanıtta.

**İpuçları:**

- Ders bölüm 4: Yeni bir alan yeni bir numara alır. `Book`'ta hangi numaralar zaten kullanılıyor?
- `Exercise1Test` alanı mesaj descriptor'ı üzerinden okur (`Book.getDescriptor().findFieldByName("year")`), bu yüzden alan var olmadan da derlenir.
- Override olmadan üretilen temel sınıf `UNIMPLEMENTED` cevabı verir.
- `SearchBooksResponse.newBuilder().addBooks(...)` kitapları toplar.

**Kabul kriterleri:** `Exercise1Test` içindeki 4 testin hepsi geçer.

**Tahmini süre:** 25 dakika

# Ödev 2 — Akış Olarak Toplu Sipariş (Orta)

**Hedef:** Bir istemci büyük bir siparişin satırlarını bir akış olarak (`PlaceOrders`) gönderir ve sonunda tek bir özet alır.

**Yapılacaklar** (`exercise2.OrderLinesObserver`):

- `TODO 2a` — Her satır için: Bilinmeyen bir kitap veya 1'den küçük bir miktar reddedilir. Aksi hâlde satır kabul edilir ve toplama fiyat × miktar ekler.
- `TODO 2b` — İstemci her şeyi gönderdiğinde tek bir `OrderSummary` ile cevap verin ve çağrıyı tamamlayın.

**İpuçları:**

- Ders bölüm 3.3 (client streaming): Durum (sayaçlar, toplam) observer'da yaşar, çağrı başına bir observer.
- `books.find(isbn)` bir `Optional<Book>` döndürür.
- Testte: 2 × 8990 + 1 × 9500 = 27480 sent.

**Kabul kriterleri:** `Exercise2Test` geçer.

**Tahmini süre:** 25 dakika

# Ödev 3 — Sonsuza Kadar Beklemeyin (Kolay)

**Hedef:** Bir ürün sayfası bir kitabın başlığını gösterir. Katalog çok yavaş olduğunda sayfa beklemek yerine başlıksız gösterilir.

**Yapılacaklar** (`exercise3.TitleLookup.titleWithin`):

- `TODO 3a` — `GetBook`'u `timeout` kadar bir deadline ile çağırın.
- `TODO 3b` — `DEADLINE_EXCEEDED`, `Optional.empty()` verir. Diğer her hata yeniden fırlatılır.

**İpuçları:**

- Ders bölüm 3.6: `stub.withDeadlineAfter(millis, TimeUnit.MILLISECONDS)`.
- `StatusRuntimeException.getStatus().getCode()`.
- Test sunucunun gecikmesini 300 ms'ye ayarlar (`bookstore.latency`) ve kısa çağrının 250 ms içinde döndüğünü kontrol eder.

**Kabul kriterleri:** `Exercise3Test` içindeki iki test de geçer.

**Tahmini süre:** 15 dakika

# Ek Meydan Okuma (İsteğe Bağlı)

Her çağrıyla bir `x-request-id` header'ı gönderen bir client interceptor'ı (`@GlobalClientInterceptor`) ve onu okuyup loga yazan bir sunucu interceptor'ı ekleyin. ID'nin sunucuya ulaştığını test edin.
