#!/usr/bin/env bash
# Creates the local Kubernetes cluster "bookstore" with kind (modules 22, 23 and the capstone) and installs the
# metrics-server, which the HorizontalPodAutoscaler needs. Safe to run again: an existing cluster is kept.
#
# Needs: Docker, kind, kubectl (Helm for the capstone).   Remove the cluster with scripts/kind-down.sh
set -euo pipefail

CLUSTER=bookstore
CONFIG="$(dirname "$0")/kind-config.yaml"
METRICS_SERVER=https://github.com/kubernetes-sigs/metrics-server/releases/download/v0.8.0/components.yaml

# prerequisites: fail early with a clear message instead of halfway through
missing=0
for tool in docker kind kubectl; do
  command -v "$tool" > /dev/null || { echo "✗ '$tool' is not installed (see module 22, section 2)" >&2; missing=1; }
done
[ "$missing" -eq 0 ] || exit 1
docker info > /dev/null 2>&1 || { echo "✗ Docker is not running" >&2; exit 1; }
command -v helm > /dev/null || echo "! 'helm' is not installed — only needed for the capstone (capstone/k8s/deploy-kind.sh)"

if kind get clusters | grep -qx "$CLUSTER"; then
  echo "Cluster '$CLUSTER' exists."
else
  kind create cluster --config "$CONFIG" --wait 120s
fi
kubectl config use-context "kind-$CLUSTER" > /dev/null

# kind's kubelets use self-signed certificates: metrics-server must accept them
kubectl apply -f "$METRICS_SERVER" > /dev/null
kubectl -n kube-system patch deployment metrics-server --type=json \
  -p '[{"op":"add","path":"/spec/template/spec/containers/0/args/-","value":"--kubelet-insecure-tls"}]' > /dev/null 2>&1 || true
kubectl -n kube-system rollout status deployment/metrics-server --timeout=180s

kubectl get nodes
