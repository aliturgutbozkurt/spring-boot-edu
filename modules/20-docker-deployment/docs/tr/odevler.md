---
title: "Modül 20 — Docker ve Deployment"
subtitle: "Ödevler"
module: "20-docker-deployment"
lang: tr-TR
date: "2026-09-25"
---

# Nasıl Çalışılır

1. Başlangıç kodu `modules/20-docker-deployment/exercise/` altındadır. `TODO` yorumlarını bulun (`Dockerfile` ve `compose.yaml` içinde de).
2. Testler Docker image'ları build eder, bu yüzden entegrasyon testleridir (`*IT`) ve jar'a ihtiyaç duyar: Onları `test` ile değil `verify` ile çalıştırın. Docker çalışıyor olmalıdır.

```bash
./mvnw -Pexercises -pl modules/20-docker-deployment/exercise -am verify
```

3. Tüm testler yeşil olduğunda ödev tamamdır.
4. Takılırsanız önce ipuçlarını, sonra `modules/20-docker-deployment/solution/` altındaki çözümü okuyun.

# Ödev 1 — Dockerfile'ı Optimize Etmek (Orta)

**Hedef:** `exercise/Dockerfile` çalışıyor, ama image yaklaşık 400 MB, tam bir JDK içeriyor, root olarak çalışıyor ve jar'ın tamamını tek bir katmana koyuyor.

**Yapılacaklar** (`exercise/Dockerfile`):

- `TODO 1a` — Çok aşamalı bir build: Jar'ı katmanlara ayırın ve onları son image'a tek tek kopyalayın.
- `TODO 1b` — Son image bir JDK değil, `jdeps` ve `jlink` ile build edilmiş bir JRE içerir.
- `TODO 1c` — Root olmayan bir kullanıcı olarak çalıştırın.
- `TODO 1d` — `java`'nın PID 1 olması için exec formunu kullanın.

**İpuçları:**

- Ders bölüm 3.2 her adımı gösterir. Önce kendiniz yazmayı deneyin, sonra karşılaştırın.
- Alpine'de `jlink --strip-debug`, `apk add binutils`'e ihtiyaç duyar.
- `Exercise1IT` kullanıcıyı (`id -u`), PID 1'i (`/proc/1/cmdline`), boyutu (< 150 MB) ve `javac` olmadığını kontrol eder.
- PID 1 testi zaten yeşildir: BusyBox `sh -c` tek bir komutla kendini değiştirir. Exec formu yine de güvenilir yoldur (her shell ile ve birden çok komutla çalışır).

**Kabul kriterleri:** `Exercise1IT` içindeki 4 testin hepsi geçer.

**Tahmini süre:** 40 dakika

# Ödev 2 — Compose ile Bir Sistem (Orta)

**Hedef:** Kitapçının veritabanına ve bir depo servisine ihtiyacı var. `exercise/compose.yaml` sistemi yalnızca kısmen tarif ediyor.

**Yapılacaklar** (`exercise/compose.yaml`):

- `TODO 2a` — PostgreSQL için bir healthcheck (`pg_isready`).
- `TODO 2b` — `nginx:1.29-alpine`'dan bir `warehouse` servisi.
- `TODO 2c` — Uygulamayı ortam değişkenleriyle yapılandırın: Datasource URL'i, kullanıcı, parola ve `BOOKSTORE_WAREHOUSE_HOST` / `BOOKSTORE_WAREHOUSE_PORT`.
- `TODO 2d` — Uygulamayı yalnızca veritabanı sağlıklı ve depo başlamış olduğunda başlatın.

**İpuçları:**

- Ders bölüm 3.6: `condition: service_healthy` ve `condition: service_started`.
- Servisler birbirlerine servis adıyla ulaşır: `jdbc:postgresql://postgres:5432/bookstore`, depo portu `80`.
- `Exercise2IT` compose dosyanızı başlatır ve `/actuator/health/readiness` için 3 dakikaya kadar bekler. Önce elle deneyin: `docker compose -f modules/20-docker-deployment/exercise/compose.yaml up --build`.

**Kabul kriterleri:** `Exercise2IT` içindeki iki test de geçer.

**Tahmini süre:** 30 dakika

# Ödev 3 — Readiness Depoya Bağlıdır (Orta)

**Hedef:** Depo olmadan siparişler teslim edilemez, bu yüzden uygulama trafik almamalıdır. Ama onu yeniden başlatmak depoyu geri getirmez.

**Yapılacaklar:**

- `TODO 3a` (`warehouse.WarehouseHealthIndicator`) — Depoya bir TCP bağlantısı çalıştığında UP (zaman aşımı 500 ms), aksi hâlde exception ile DOWN. Adresi bir detay olarak ekleyin.
- `TODO 3b` (`application.yaml`) — `warehouse` health bileşenini liveness'a değil, readiness grubuna ekleyin.

**İpuçları:**

- try-with-resources içinde `new Socket().connect(new InetSocketAddress(host, port), 500)`.
- `Health.up().withDetail(...).build()` ve `Health.down().withException(e).build()`.
- Bileşen adı, `HealthIndicator` olmadan bean adından gelir: `warehouse`.

**Kabul kriterleri:** `Exercise3IT` geçer.

**Tahmini süre:** 25 dakika

# Ek Meydan Okuma (İsteğe Bağlı)

Ödev 1'deki Dockerfile'ınıza readiness probe'unu kullanan bir `HEALTHCHECK` ekleyin ve compose'da ikinci bir uygulama örneğinin `depends_on`'unu birincisini `condition: service_healthy` ile bekleyecek şekilde değiştirin. Ardından depoyu durdurun (`docker compose stop warehouse`) ve `docker compose ps`'i izleyin.
