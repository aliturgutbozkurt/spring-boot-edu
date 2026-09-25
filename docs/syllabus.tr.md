---
title: "Spring Boot 4 — Temellerden Production'a"
subtitle: "Müfredat"
module: "syllabus"
lang: tr-TR
date: "2026-09-25"
---

# 1. Kurs Hakkında

25 modül ve bir bitirme projesinden oluşan, Spring Boot 4.1 ve Java 27 üzerine uygulamalı bir kurs. Her modül aynı alan etrafında kurulmuş küçük, çalıştırılabilir bir Maven projesidir — bir kitapçı (`Book`, `Author`, `Customer`, `Order`, `Review`) —; böylece her hafta yeni bir alan değil, Spring öğrenirsiniz.

Her modülde şunlar vardır:

| Bölüm | Ne olduğu |
|---|---|
| `lesson/` | testleriyle çalıştırılabilir örnekler — her zaman yeşil |
| `docs/` | Türkçe ve İngilizce ders notları ve ödevler (Markdown ve PDF) |
| `exercise/` | `TODO`'lu başlangıç kodu — siz çözene kadar testleri kırmızı |
| `solution/` | aynı testlerle referans çözüm — yeşil |
| `README.md`, `requests.http` | modülün nasıl çalıştırılacağı, IDE için HTTP örnekleri |

**Hedef kitle:** Java bilen ve Spring'e yeni başlayan, ya da Spring'i daha önce kullanmış ve 2026'nın güncel durumunu (Spring Boot 4, Spring Framework 7, Java 27) öğrenmek isteyen geliştiriciler.

**Biçim:** kendi kendine öğrenme ya da sınıf. Aşağıdaki planda bir hafta yaklaşık 9 saattir: ders notları ve örnekler (≈ %60), ödevler (≈ %40).

# 2. Ön Koşullar ve Kurulum

**Bilgi:**

- Java: sınıflar, interface'ler, generic'ler, koleksiyonlar, lambda'lar ve stream'ler
- temel SQL (`SELECT`, `JOIN`, `INSERT`) ve HTTP (metotlar, durum kodları, JSON)
- Git ve terminal

**Makine:**

| Araç | Ne için gerekli |
|---|---|
| JDK 27 | her şey (Maven wrapper Maven 3.9'u getirir) |
| ≥ 8 GB belleğe sahip Docker | modül 05'ten itibaren veritabanları ve broker'lar (Testcontainers, Docker Compose) |
| bir IDE (IntelliJ IDEA, VS Code) | `requests.http` dosyaları, debug |
| kind, kubectl, Helm | modül 22 ve 23, Kubernetes'te bitirme projesi |
| modeller için ≈ 2 GB boş disk | modül 24 (Ollama) |

Modül 00 kurulumu adım adım anlatır; depo kökünde `./mvnw verify` her şeyin çalıştığını kanıtlar.

# 3. Kazanımlar

Kursun sonunda şunları yapabileceksiniz:

1. Spring Boot 4 ile REST, GraphQL, WebSocket ve gRPC API'leri yazmak — doğrulamalı ve RFC 9457 hata cevaplarıyla
2. veriyi PostgreSQL (JDBC, JPA), MongoDB, Redis, Hazelcast ve Elasticsearch'te saklamak — ve aralarında seçim yapmak
3. servisleri güvenilir biçimde bağlamak: yeniden denemeli HTTP istemcileri, transactional outbox ve idempotent consumer'larla Kafka
4. uygulamaları Spring Security ile korumak: form login, JWT resource server, OAuth2 ve metot güvenliği
5. her seviyede test etmek: unit testler, slice testler, Testcontainers, sözleşme fake'leri, uçtan uca testler
6. uygulamaları gözlemlenebilir (OpenTelemetry ve Grafana ile metrik, trace, log) ve production'a hazır yapmak (Docker imajları, native imajlar, Kubernetes, Helm)
7. büyük uygulamaları Spring Modulith ve Spring Cloud ile yapılandırmak, Spring AI ile yapay zekâ özellikleri eklemek
8. hepsini birden çok servisten oluşan bir sistemde birleştirmek (bitirme projesi)

# 4. Haftalık Plan

Sıralama modül numaralarını izler; her modül öncekilerin üzerine kurulur (bkz. bölüm 5).

| Hafta | Modüller | Ana konular | Saat |
|---|---|---|---|
| 1 | 00, 01 | kurulum, modern Java 21 → 27; IoC ve dependency injection | 7 |
| 2 | 02, 03 | yapılandırma, auto-configuration, bir starter; Web MVC ile REST | 9 |
| 3 | 04, 05 | `RestClient`, HTTP interface'leri, `@Retryable`; JDBC, Flyway, Testcontainers | 9 |
| 4 | 06, 07 | JPA ve Hibernate; MongoDB | 11 |
| 5 | 08, 09 | Redis ve cache; Hazelcast (map'ler, kilitler, entry processor'lar) | 8 |
| 6 | 10, 11 | Elasticsearch; Kafka, outbox, Kafka Streams | 11 |
| 7 | 12, 13 | Spring Security, JWT, OAuth2; reaktif programlama, WebFlux, R2DBC | 12 |
| 8 | 14, 15 | test stratejileri; OpenTelemetry ve Grafana ile gözlemlenebilirlik | 10 |
| 9 | 16, 17 | async, zamanlama, Spring Batch; GraphQL ve WebSocket | 10 |
| 10 | 18, 19 | Spring Modulith; AOT işleme, JVM AOT cache, GraalVM native image | 8 |
| 11 | 20, 21 | Docker imajları, jlink, Buildpacks; gRPC | 8 |
| 12 | 22, 23 | kind ile Kubernetes; Spring Cloud (Gateway, Config, LoadBalancer, Circuit Breaker) | 11 |
| 13 | 24, bitirme | Spring AI (chat, structured output, RAG, tool'lar, MCP); bitirme projesine başlangıç | 5 + 4 |
| 14 | bitirme | kitapçı platformu: rehber, ödevler, Kubernetes | 6–8 |

> [!TIP]
> Zamanınız kısıtlı mı? 1–8. haftalar günlük Spring işinin çekirdeğidir. 16–24 arası modüller, ön koşulları tamamlandıktan sonra herhangi bir sırayla alınabilir.

# 5. Modüllere Genel Bakış

## 5.1 Temeller

| Modül | Konu | Ön koşul | Saat |
|---|---|---|---|
| 00 | Kurulum ve modern Java (21 → 27) | temel Java | 3 |
| 01 | Spring core container: IoC ve dependency injection | 00 | 4 |
| 02 | Yapılandırma ve auto-configuration | 01 | 4 |
| 03 | Web MVC ile REST API'ler | 01, 02 | 5 |
| 04 | HTTP istemcileri ve dayanıklılık | 01–03 | 4 |

## 5.2 Veri

| Modül | Konu | Ön koşul | Saat |
|---|---|---|---|
| 05 | Spring JDBC ve PostgreSQL | 01–03, temel SQL | 5 |
| 06 | Spring Data JPA ve Hibernate | 05 | 6 |
| 07 | Spring Data MongoDB | 05, 06 | 5 |
| 08 | Redis ve cache | 01–03 | 4 |
| 09 | Hazelcast ile dağıtık veri | 08 | 4 |
| 10 | Elasticsearch ile arama | 06 | 5 |

## 5.3 Entegrasyon ve Güvenlik

| Modül | Konu | Ön koşul | Saat |
|---|---|---|---|
| 11 | Kafka ile mesajlaşma | 06 | 6 |
| 12 | Spring Security ile güvenlik | 03, 06 | 6 |
| 13 | Reaktif programlama ve WebFlux | 03, 07 | 6 |
| 14 | Spring Boot uygulamalarını test etmek | 06 | 5 |
| 15 | Gözlemlenebilirlik: metrikler, trace'ler, loglar | 03 | 5 |
| 16 | Async, zamanlama ve Spring Batch | 06 | 5 |
| 17 | GraphQL ve WebSocket | 06, 13 | 5 |
| 18 | Spring Modulith | 06, 11 | 4 |

## 5.4 Production ve Ötesi

| Modül | Konu | Ön koşul | Saat |
|---|---|---|---|
| 19 | Native image ve performans | 03, 05 | 4 |
| 20 | Docker ve deployment | 15 | 4 |
| 21 | gRPC | 03 | 4 |
| 22 | Kubernetes | 20 | 5 |
| 23 | Spring Cloud | 04, 15, 22 | 6 |
| 24 | Spring AI | 06 | 5 |
| bitirme | Kitapçı platformu: 4 servis + gateway, tüm teknolojiler bir arada | hepsi | 8–12 |

# 6. Değerlendirme

- **Ödevler:** her modülde en az üç. Bir ödev, testleri yeşil olduğunda tamamdır — testler kabul kriterleridir ve çözümdeki testlerle aynıdır. Her ödev tahmini süresini ve zorluğunu belirtir.
- **Bitirme projesi:** çalışan platform üzerinde üç ödev (idempotency, telafili iptal, yeniden deneme) ve isteğe bağlı ek görevler. Uçtan uca test yeşil kalmalıdır.
- **Sınıf içi bir kurs için önerilen notlandırma:** ödevler %50, bitirme ödevleri %30, bir ek görevin kısa sunumu %20.

# 7. Teknoloji Yığını

| Alan | Sürüm |
|---|---|
| Java | 27 |
| Spring Boot | 4.1.1 (Spring Framework 7.0, Spring Security 7.1, Spring Data 2026.0) |
| Spring Cloud / Spring AI / Spring Modulith | 2025.1.3 / 2.0.1 / 2.1.1 |
| Build ve test | Maven 3.9 (wrapper), JUnit 6, AssertJ, Mockito, Testcontainers 2 |
| Altyapı (Docker) | PostgreSQL 18, MongoDB 8, Elasticsearch 9, Redis 8, Kafka 4 (KRaft), Hazelcast 5.5, Ollama, Grafana LGTM |
| Deployment | Docker (jlink imajları), GraalVM native image, kind, Kustomize, Helm |

# 8. Öğrenciler ve Eğitmenler için İpuçları

- Önce dersi çalıştırın (`./mvnw -pl modules/NN-…/lesson spring-boot:run`), sonra notları kodun yanında okuyun: her örnek ait olduğu ders bölümünü belirtir.
- Ödevleri çözümü okumadan önce yapın. Her ödevdeki ipuçları, yardımcı olacak ders bölümünü gösterir.
- Her dersin "Sık Yapılan Hatalar" bölümü sınıf içi tartışmalar için iyi bir başlangıç noktasıdır.
- Sınıf içi bir kursta her haftaya modülün mimarisiyle (dersin 2. bölümü) başlayın ve canlı demolar için `requests.http` dosyasını kullanın.
