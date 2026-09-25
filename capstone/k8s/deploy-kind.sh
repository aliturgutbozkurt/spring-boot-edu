#!/usr/bin/env bash
# ADR-8 — installs the whole platform with Helm on the local kind cluster "bookstore".
# Needs Docker (≈ 6 GB memory for the cluster), kind, kubectl, helm. Stop the compose system first.
#   capstone/k8s/deploy-kind.sh            build, load the images into kind, helm upgrade --install
#   capstone/k8s/deploy-kind.sh --set infra.lgtm.enabled=false    extra arguments go to helm (here: no Grafana)
#   helm -n bookstore uninstall bookstore  remove it again
set -euo pipefail
cd "$(dirname "$0")/../.."                                   # repository root
NAMESPACE=bookstore

scripts/kind-up.sh

# 1. the four service images (same Dockerfile as compose.yaml)
./mvnw -q -pl capstone/catalog-service,capstone/order-service,capstone/search-service,capstone/gateway -am \
    package -DskipTests
JAR_SOURCE=host docker compose -f capstone/compose.yaml build

# 2. kind nodes cannot see the local Docker images: load them (imagePullPolicy IfNotPresent → never pulled)
IMAGES=$(helm template bookstore capstone/k8s/helm/bookstore "$@" | grep -E '^\s+image:' | awk '{print $2}' | sort -u)
for image in $IMAGES; do
  docker image inspect "$image" > /dev/null 2>&1 || docker pull -q "$image"
  kind load docker-image --name bookstore "$image"
done

# 3. install or upgrade; --wait returns when every Deployment is ready
helm upgrade --install bookstore capstone/k8s/helm/bookstore \
    --namespace "$NAMESPACE" --create-namespace --wait --timeout 15m "$@"
kubectl -n "$NAMESPACE" get pods
