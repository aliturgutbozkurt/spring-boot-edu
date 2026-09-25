#!/usr/bin/env bash
# Lesson 3.7 — the catalog (2 pods) and the order service on kind, with Spring Cloud Kubernetes.
# Needs: the images from up.sh (or ./mvnw package + docker build) and the cluster from scripts/kind-up.sh
set -euo pipefail
cd "$(dirname "$0")/../.."                                   # repository root
M=modules/23-spring-cloud
for service in catalog-service order-service; do
  docker tag "springbootedu/cloud-$service:latest" "springbootedu/cloud-$service:1.0.0"
  kind load docker-image "springbootedu/cloud-$service:1.0.0" --name bookstore
done
kubectl apply -k "$M/k8s"
kubectl -n bookstore-cloud rollout status deployment/catalog-service --timeout=180s
kubectl -n bookstore-cloud rollout status deployment/order-service --timeout=180s
kubectl -n bookstore-cloud get pods
