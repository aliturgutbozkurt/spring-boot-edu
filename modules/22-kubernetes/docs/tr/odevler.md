---
title: "Modül 22 — Kubernetes"
subtitle: "Ödevler"
module: "22-kubernetes"
lang: tr-TR
date: "2026-09-25"
---

# Nasıl Çalışılır

1. Ödev manifest'leri `modules/22-kubernetes/exercise/k8s/` altındadır. `TODO` yorumlarını bulun.
2. Testler YAML dosyalarınızı okur ve kuralları küme olmadan kontrol eder:

```bash
./mvnw -Pexercises -pl modules/22-kubernetes/exercise -am test
```

3. Ardından manifest'lerinizi kind kümesinde deneyin (her ödevin bir "Deneyin" kısmı vardır). Kendi `bookstore-exercise` namespace'lerinde çalışırlar ve dersin image'ını kullanırlar (`modules/22-kubernetes/deploy.sh`'ı bir kez çalıştırın, image'ı kind'a yükler):

```bash
kubectl apply -k modules/22-kubernetes/exercise/k8s
kubectl -n bookstore-exercise rollout status deployment/bookstore
```

4. Tüm testler yeşil olduğunda ve "Deneyin" kısımları tarif edildiği gibi davrandığında ödev tamamdır.
5. Takılırsanız önce ipuçlarını, sonra `modules/22-kubernetes/solution/k8s/` altındaki çözümü okuyun.

# Ödev 1 — Pod'lara Ulaşan Yapılandırma Değişiklikleri (Kolay)

**Hedef:** Mağaza adı elle yazılmış bir ConfigMap'ten (`configmap.yaml`) geliyor. Onu değiştirin, uygulayın ve çalışan pod'lar hâlâ eski adı gösterir.

**Yapılacaklar** (`exercise/k8s/`):

- `TODO 1` — `configmap.yaml`'yi `kustomization.yaml` içinde `bookstore-config` adlı bir `configMapGenerator` ile değiştirin (aynı `BOOKSTORE_SHOP_NAME` anahtarı) ve `configmap.yaml`'yi kaynaklardan çıkarın.

**İpuçları:**

- Ders bölüm 3.2. Deployment `configMapRef: name: bookstore-config`'i korur: Kustomize onu üretilen adla değiştirir.
- Eski yol neden çalışmadı? Ortam değişkenleri yalnızca container başladığında okunur.

**Deneyin:** Uygulayın, mağaza adını `kustomization.yaml`'de değiştirin, tekrar uygulayın ve `kubectl -n bookstore-exercise get pods -w`'yi izleyin: Yeni pod'lar yeni adla başlar (`port-forward` + `/api/shop`).

**Kabul kriterleri:** `Exercise1Test` içindeki 3 testin hepsi geçer.

**Tahmini süre:** 20 dakika

# Ödev 2 — Bozuk Bir Sürüm Zarar Vermemeli (Orta)

**Hedef:** Başlamayan yeni bir sürüm trafiği elden almamalıdır ve geri dönebilmelisiniz.

**Yapılacaklar** (`exercise/k8s/deployment.yaml`):

- `TODO 2a` — Hazır bir pod'u, yerine gelen hazır olmadan asla kaldırmayan, her seferinde bir yeni pod başlatan bir rolling update.
- `TODO 2b` — `/actuator/health/readiness` üzerinde bir readiness probe.
- `TODO 2c` — En az 3 eski sürümü saklayın (`revisionHistoryLimit`).

**İpuçları:** Ders bölüm 3.3 ve 3.6 (`maxUnavailable`, `maxSurge`).

**Deneyin:** Bozuk bir image ayarlayın, pod'lara bakın, sonra geri alın:

```bash
kubectl -n bookstore-exercise set image deployment/bookstore bookstore=springbootedu/kubernetes:broken
kubectl -n bookstore-exercise get pods
kubectl -n bookstore-exercise rollout undo deployment/bookstore
```

Not edin: Bozuk pod başarısız olurken kaç eski pod hazırdı?

**Kabul kriterleri:** `Exercise2Test` içindeki 3 testin hepsi geçer ve bozuk rollout sırasında eski pod'lar hazır kaldı.

**Tahmini süre:** 30 dakika

# Ödev 3 — Yük Altında Ölçekleme (Orta)

**Hedef:** CPU yüküne göre 1 ile 4 arasında pod.

**Yapılacaklar:**

- `TODO 3a` (`deployment.yaml`) — `200m` CPU isteyin (request).
- `TODO 3b` — `hpa.yaml`'de (kaynaklara eklenmiş) bir otomatik ölçekleyici: `bookstore` Deployment'ını hedefleyin, 1 ile 4 kopya, ortalama %50 CPU.

**İpuçları:**

- Ders bölüm 3.6. CPU request'i olmadan HPA `cpu: <unknown>` gösterir ve hiçbir şey yapmaz.
- metrics-server `scripts/kind-up.sh`'tan gelir.

**Deneyin:** Ders bölüm 3.6'daki yük pod'unu `bookstore-exercise` namespace'inde başlatın ve `kubectl -n bookstore-exercise get hpa -w`'yi izleyin.

**Kabul kriterleri:** `Exercise3Test` içindeki iki test de geçer ve yük altında kopya sayısı artar.

**Tahmini süre:** 30 dakika

# Ek Meydan Okuma (İsteğe Bağlı)

Bir `PodDisruptionBudget` (`minAvailable: 1`) ekleyin ve kind node'unu boşaltın (`kubectl drain bookstore-control-plane --ignore-daemonsets --delete-emptydir-data`). Bütçe neyi değiştirir? `kubectl uncordon bookstore-control-plane` ile geri alın.
