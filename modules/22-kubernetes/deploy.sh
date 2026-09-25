#!/usr/bin/env bash
# Lesson 3.1 — builds the image, loads it into kind and deploys an overlay; then waits and calls the API once.
#
# Needs: JDK 27 (JAVA_HOME), Docker, kind, kubectl and the cluster from scripts/kind-up.sh
# Usage: modules/22-kubernetes/deploy.sh [dev|prod]      (default: dev)
set -euo pipefail

OVERLAY="${1:-dev}"
cd "$(dirname "$0")/../.."                                   # repository root
MODULE=modules/22-kubernetes
IMAGE=springbootedu/kubernetes:1.0.0

./mvnw -q -pl "$MODULE/lesson" package -DskipTests
docker build -q -t "$IMAGE" "$MODULE/lesson" > /dev/null
kind load docker-image "$IMAGE" --name bookstore            # kind's node cannot see the images of your Docker

kubectl apply -k "$MODULE/k8s/overlays/$OVERLAY"
kubectl -n bookstore rollout status statefulset/postgres --timeout=180s
kubectl -n bookstore rollout status deployment/bookstore --timeout=300s
kubectl -n bookstore get pods

# a short smoke test through the Service
kubectl -n bookstore port-forward service/bookstore 18080:80 > /dev/null 2>&1 &
FORWARD=$!
trap 'kill $FORWARD 2> /dev/null || true' EXIT
for _ in $(seq 1 30); do curl -sf localhost:18080/actuator/health/readiness > /dev/null && break; sleep 1; done
curl -sf localhost:18080/api/shop; echo
curl -sf localhost:18080/api/books | head -c 200; echo
