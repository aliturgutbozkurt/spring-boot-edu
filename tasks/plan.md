# Uygulama Planı — Spring Boot Eğitim Projesi

> Kaynak: [SPEC.md](../SPEC.md) · Görevler: [todo.md](todo.md)
> Durum: **ONAYLANDI** (2026-09-23)

## Yaklaşım

1. **Önce altyapı, sonra tek bir pilot modül.** Build iskeleti, Docker Compose, PDF hattı ve şablonlar kurulur. Ardından **bir modül uçtan uca** (kod + test + TR/EN doküman + PDF + ödev + çözüm) bitirilir. Şablon bu pilotta doğrulanmadan diğer modüllere geçilmez. Yanlış bir şablonu 25 modüle kopyalamak en pahalı hata olur.
2. **Her modül dikey bir dilimdir.** Her modül kendi içinde tamamlanır (DoD), sonra bir sonrakine geçilir. Kodu önce tüm modüllere, dokümanı sonra yazmak yok.
3. **Her modül üç görevden oluşur:** (a) lesson kodu + testler → (b) exercise + solution → (c) TR/EN doküman + PDF. Doküman en son yazılır, çünkü parçacıkları derlenmiş koddan alır.
4. **Altyapı teknolojileri, ihtiyaç duyan ilk modülde** compose + Testcontainers ile eklenir (PostgreSQL M05'te, Redis M08'de vb.).

## Bileşenler ve Bağımlılıklar

```
T0 Temel (build, compose, scriptler, şablonlar, CI)
 └─ P1 Pilot: 01-core-container (şablonu doğrular)
     ├─ Faz 2 Çekirdek: 00, 02, 03, 04
     │   └─ Faz 3 Veri: 05 → 06 → (07 paralel)
     │       └─ Faz 4 Altyapı: 08 → 09, 10, 11 (10 ve 11 paralel)
     │           └─ Faz 5 Platform: 12, 13, 14, 15
     │               └─ Faz 6 İleri: 16, 17, 18, 19, 20
     │                   └─ Faz 7 Dağıtık Sistemler: 21, 22 → 23, 24
     │                       └─ Faz 8 Capstone + müfredat + final review
```

## Fazlar ve Kontrol Noktaları

### Faz 0 — Temel (T0.x)
Git, Maven wrapper, root aggregator + `build-parent` (Boot 4.1.1, Java 27, enforcer, surefire/failsafe, `-Pexercises` profili), profilli root `compose.yaml` (6 servis + LGTM), `build-pdfs.sh` (Dockerized Pandoc, Türkçe font), doküman şablonları, `check-module.sh`, `new-module.sh`, GitHub Actions.
**Checkpoint 0:** Boş bir örnek modül iskeletten üretilir, `./mvnw verify` yeşil, örnek MD'den TR ve EN PDF üretilir (Türkçe karakterler doğru), `docker compose --profile all up` tüm servisleri healthy getirir.

### Faz 1 — Pilot modül (01-core-container)
Tek modülün tamamı. Ardından şablon, doküman yapısı ve ödev formatı gözden geçirilir; gerekirse şablon güncellenir.
**Checkpoint 1 (insan onayı):** Kullanıcı pilot modülün PDF'ini, kodunu ve ödevini inceler, formatı onaylar. **Bu onay olmadan Faz 2 başlamaz.**

### Faz 2 — Çekirdek (00, 02, 03, 04)
Modern Java, konfigürasyon/auto-config/özel starter, Web MVC + API versioning, HTTP client'lar + resilience.
**Checkpoint 2:** 4 modül DoD'u geçer; CI yeşil.

### Faz 3 — Veri (05, 06, 07)
PostgreSQL (JDBC → JPA) ve MongoDB. Flyway ile şema yönetimi burada kurulur; sonraki modüller bunu yeniden kullanır.
**Checkpoint 3:** Testcontainers IT'leri CI'da çalışır (Docker'lı runner).

### Faz 4 — Cache, Arama, Mesajlaşma (08, 09, 10, 11)
Redis, Hazelcast, Elasticsearch, Kafka. 10 ve 11 birbirinden bağımsızdır, paralel yazılabilir.
**Checkpoint 4:** Her teknoloji hem `spring-boot:run` (compose) hem test (Testcontainers) yolunda çalışır.

### Faz 5 — Platform (12, 13, 14, 15)
Security, Reactive, Testing derinlemesine, Observability.
**Checkpoint 5:** Güvenlik modülü `security-auditor` ajanı ile gözden geçirilir.

### Faz 6 — İleri Konular (16–20)
Async/Batch, GraphQL/WebSocket, Modulith, Native/AOT, Docker/Deployment.
**Checkpoint 6:** Native image build'i CI'da ayrı (manuel tetiklenen) bir iş olarak çalışır.

### Faz 7 — Dağıtık Sistemler ve AI (21, 22, 23, 24)
gRPC, Kubernetes (kind), Spring Cloud (Gateway, Config, Circuit Breaker, Spring Cloud Kubernetes), Spring AI (Ollama + pgvector). 21 ve 24 bağımsızdır; 23, 22'ye bağlıdır.
**Checkpoint 7:** kind cluster script'i temiz makinede çalışır; AI testleri gerçek LLM'e bağlı değildir.

### Faz 8 — Capstone ve Kapanış
Tüm teknolojileri kullanan çok servisli Kitapçı platformu (Gateway + gRPC + Kafka; Compose ve Helm/kind), uçtan uca test, genel müfredat (syllabus) TR/EN + PDF, kök README, final review.
**Checkpoint 8:** SPEC'teki 7 başarı kriterinin hepsi karşılanır.

## Riskler ve Önlemler

| Risk | Etki | Önlem |
|---|---|---|
| Java 27 desteği bazı araçlarda eksik olabilir (JaCoCo, GraalVM, Docker base image, bazı Maven plugin'leri) | Build kırılır | T0'da hepsi smoke test ile doğrulanır; eksik olan için alternatif (jlink runtime, JaCoCo'suz build) belirlenip SPEC'e yazılır |
| Makinede Maven JDK 23 ile çalışıyor | "release 27 not supported" hatası | Maven Wrapper + enforcer (`requireJavaVersion [27,)`) + CLAUDE.md'de `JAVA_HOME` notu |
| Elasticsearch/Kafka bellek tüketimi | Öğrenci makinesinde yavaşlık | Compose profilleri; ES heap 512 MB; README'de minimum RAM |
| TR/EN dokümanların kayması | Tutarsız içerik | `check-module.sh` başlık ve kod bloğu sayısı eşleştirmesi; aynı commit kuralı |
| Dokümandaki kodun kaynaktan farklılaşması | Çalışmayan örnek | Parçacıklar dosya + satır referanslı; script ile doğrulama |
| Spring Boot 4.2 GA (Kasım 2026) | İçerik eskir | Sürüm tek yerde (`build-parent`); yükseltme ayrı görev |
| Kapsam çok büyük (25 modül + capstone) | Bitmeyen proje | Dikey dilimler: her modül tek başına yayınlanabilir; AI modülü en sona |
| Testcontainers CI süresi | Yavaş CI | Container reuse, modül bazlı paralel matrix |
| Ollama modelleri büyük (GB'lar) | Yavaş kurulum, CI'da çalışmaz | Küçük model (ör. 1B), testlerde mock `ChatModel`; gerçek model sadece manuel çalıştırmada |
| Kubernetes öğrenci makinesinde ağır | Kurulum sorunları | kind + tek node, kaynak limitli manifestler; `kind-up.sh` önkoşul kontrolü yapar |
| Spring Cloud release train'i Boot 4.1 ile uyumsuz sürüme kayabilir | Build kırılır | Sürüm `build-parent`'ta tek yerde; start.spring.io uyumluluk aralığına göre güncellenir |

## Paralelleştirme

- T0 içinde: compose, PDF hattı ve Maven iskeleti bağımsızdır.
- Pilot onayından sonra: 07 ∥ 05/06; 10 ∥ 11; 15 ∥ 12–14; 19 ∥ 16–18; 21 ∥ 22; 24 ∥ 22–23.
- Her modülde (c) doküman görevi, (a) bittikten sonra (b) ile paralel yürüyebilir.

## Doğrulama (her modülde)

```bash
./mvnw -pl modules/<id>/lesson,modules/<id>/solution -am verify
./mvnw -Pexercises -pl modules/<id>/exercise test-compile
./scripts/build-pdfs.sh <id>
./scripts/check-module.sh --strict <id>
```
