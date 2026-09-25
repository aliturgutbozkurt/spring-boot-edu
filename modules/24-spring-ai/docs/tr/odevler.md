---
title: "Modül 24 — Spring AI"
subtitle: "Ödevler"
module: "24-spring-ai"
lang: tr-TR
date: "2026-09-25"
---

# Nasıl Çalışılır

1. Başlangıç kodu `modules/24-spring-ai/exercise/` altındadır. `TODO` yorumlarını bulun.
2. Testler sahte bir `ChatModel` ve anahtar kelime tabanlı bir `EmbeddingModel` (ders bölüm 3.8) ile Testcontainers'tan pgvector'lü PostgreSQL kullanır: Docker çalışmalıdır ama bir dil modeline gerek yoktur.

```bash
./mvnw -Pexercises -pl modules/24-spring-ai/exercise -am test
```

3. Tüm testler yeşil olduğunda ödev tamamdır. Ardından isterseniz kodunuzu gerçek modelle deneyin (dersin `spring-boot:run`'ı Ollama'nın nasıl başlatıldığını gösterir).
4. Takılırsanız önce ipuçlarını, sonra `modules/24-spring-ai/solution/` altındaki çözümü okuyun.

# Ödev 1 — Bir Kitap Öneri Asistanı (Kolay)

**Hedef:** Bir müşteri ilgi alanlarını söyler ve asistan, bir record listesi olarak belirli sayıda kitap önerir.

**Yapılacaklar** (`exercise1.SuggestionAssistant.suggest`):

- `TODO 1a` — Kullanıcı mesajı `interests` ve `count` parametreleriyle `prompts/suggest.st` şablonundan gelir.
- `TODO 1b` — Cevap bir `List<BookSuggestion>` olur.

**İpuçları:**

- Ders bölüm 3.3: `.user(user -> user.text(template).param(...))`.
- Bir liste generic tipe ihtiyaç duyar: `.entity(new ParameterizedTypeReference<List<BookSuggestion>>() { })`.

**Kabul kriterleri:** `Exercise1Test` içindeki iki test de geçer.

**Tahmini süre:** 20 dakika

# Ödev 2 — RAG ile Müşteri SSS (Orta)

**Hedef:** Kargo, iade ve ödeme hakkındaki sorular modelin hayal gücünden değil, mağazanın SSS'inden (`src/main/resources/faq/*.md`) cevaplanır.

**Yapılacaklar** (`exercise2` paketi):

- `TODO 2a` (`FaqIngestion.ingest`) — Her SSS dosyasını `collection` = `faq` metadata'sıyla bir doküman olarak okuyun, vector store'a ekleyin ve kaç tane eklendiğini döndürün. İki kez ingest etmek kopya oluşturmamalıdır.
- `TODO 2b` (`FaqAssistant`) — En iyi eşleşen SSS kaydı (yalnızca bir tane) her prompt'un parçası olmalıdır.

**İpuçları:**

- Ders bölüm 3.6: `TextReader`, `getCustomMetadata()`, `vectorStore.delete("collection == 'faq'")`.
- Metinler kısadır: splitter gerekmez.
- `QuestionAnswerAdvisor.builder(vectorStore).searchRequest(SearchRequest.builder().topK(1).build()).build()`.

**Kabul kriterleri:** `Exercise2Test` içindeki 3 testin hepsi geçer.

**Tahmini süre:** 30 dakika

# Ödev 3 — Siparişim Nerede? (Orta)

**Hedef:** Destek asistanı bir siparişin durumunu bir tool ile sorgulayabilir.

**Yapılacaklar** (`exercise3` paketi):

- `TODO 3a` (`OrderTools.orderStatus`) — Metodu bir tool yapın: Model için bir açıklama (ne döndürdüğü, ne zaman kullanılacağı) ve parametrenin açıklaması (sipariş ID'si, örneğin `A-1001`).
- `TODO 3b` (`SupportAssistant`) — Model `OrderTools`'un tool'larını çağırabilir.

**İpuçları:**

- Ders bölüm 3.5: `@Tool(description = …)`, `@ToolParam(description = …)`, `.defaultTools(…)`.
- `Exercise3Test` tool tanımını `ToolCallbacks.from(tools)` ile kontrol eder: ad, açıklama ve parametre şemasındaki örnek.

**Kabul kriterleri:** `Exercise3Test` içindeki 3 testin hepsi geçer.

**Tahmini süre:** 20 dakika

# Ek Meydan Okuma (İsteğe Bağlı)

Bir siparişi iptal eden bir tool ekleyin. Doğrudan iptal etmemelidir: Bir onay kodu döndürür ve yalnızca bu kodla yapılan ikinci bir çağrı iptal eder. Bu tasarımın hangi prompt injection'a karşı koruduğunu not edin.
