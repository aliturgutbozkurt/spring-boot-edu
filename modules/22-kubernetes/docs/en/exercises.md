---
title: "Module 22 — Kubernetes"
subtitle: "Exercises"
module: "22-kubernetes"
lang: en-US
date: "2026-09-25"
---

# How to Work

1. The exercise manifests are in `modules/22-kubernetes/exercise/k8s/`. Find the `TODO` comments.
2. The tests read your YAML files and check the rules, without a cluster:

```bash
./mvnw -Pexercises -pl modules/22-kubernetes/exercise -am test
```

3. Then try your manifests in the kind cluster (each exercise has a "Try it" part). They run in their own namespace `bookstore-exercise` and use the lesson's image (run `modules/22-kubernetes/deploy.sh` once, it loads the image into kind):

```bash
kubectl apply -k modules/22-kubernetes/exercise/k8s
kubectl -n bookstore-exercise rollout status deployment/bookstore
```

4. The exercise is done when all tests are green and the "Try it" parts behave as described.
5. If you get stuck, read the hints first, then the solution in `modules/22-kubernetes/solution/k8s/`.

# Exercise 1 — Configuration Changes That Reach the Pods (Easy)

**Goal:** the shop name comes from a hand-written ConfigMap (`configmap.yaml`). Change it, apply, and the running pods still show the old name.

**Tasks** (`exercise/k8s/`):

- `TODO 1` — replace `configmap.yaml` with a `configMapGenerator` named `bookstore-config` in `kustomization.yaml` (the same key `BOOKSTORE_SHOP_NAME`), and remove `configmap.yaml` from the resources.

**Hints:**

- Lesson section 3.2. The Deployment keeps `configMapRef: name: bookstore-config`: Kustomize replaces it with the generated name.
- Why did the old way not work? Environment variables are read only when the container starts.

**Try it:** apply, change the shop name in `kustomization.yaml`, apply again, and watch `kubectl -n bookstore-exercise get pods -w`: new pods start with the new name (`port-forward` + `/api/shop`).

**Acceptance criteria:** all 3 tests in `Exercise1Test` pass.

**Estimated time:** 20 minutes

# Exercise 2 — A Broken Version Must Not Hurt (Medium)

**Goal:** a new version that does not start must not take traffic away, and you must be able to go back.

**Tasks** (`exercise/k8s/deployment.yaml`):

- `TODO 2a` — a rolling update that never removes a ready pod before its replacement is ready, one new pod at a time.
- `TODO 2b` — a readiness probe on `/actuator/health/readiness`.
- `TODO 2c` — keep at least 3 old versions (`revisionHistoryLimit`).

**Hints:** lesson sections 3.3 and 3.6 (`maxUnavailable`, `maxSurge`).

**Try it:** set a broken image, look at the pods, then roll back:

```bash
kubectl -n bookstore-exercise set image deployment/bookstore bookstore=springbootedu/kubernetes:broken
kubectl -n bookstore-exercise get pods
kubectl -n bookstore-exercise rollout undo deployment/bookstore
```

Write down: how many old pods were ready while the broken pod failed?

**Acceptance criteria:** all 3 tests in `Exercise2Test` pass, and the old pods stayed ready during the broken rollout.

**Estimated time:** 30 minutes

# Exercise 3 — Scale Under Load (Medium)

**Goal:** between 1 and 4 pods, depending on CPU load.

**Tasks:**

- `TODO 3a` (`deployment.yaml`) — request `200m` CPU.
- `TODO 3b` — an autoscaler in `hpa.yaml` (added to the resources): target the Deployment `bookstore`, 1 to 4 replicas, 50 % average CPU.

**Hints:**

- Lesson section 3.6. Without a CPU request, the HPA shows `cpu: <unknown>` and does nothing.
- The metrics-server comes from `scripts/kind-up.sh`.

**Try it:** start the load pod of lesson section 3.6 in the namespace `bookstore-exercise` and watch `kubectl -n bookstore-exercise get hpa -w`.

**Acceptance criteria:** both tests in `Exercise3Test` pass, and the number of replicas goes up under load.

**Estimated time:** 30 minutes

# Extra Challenge (Optional)

Add a `PodDisruptionBudget` (`minAvailable: 1`) and drain the kind node (`kubectl drain bookstore-control-plane --ignore-daemonsets --delete-emptydir-data`). What does the budget change? Undo it with `kubectl uncordon bookstore-control-plane`.
