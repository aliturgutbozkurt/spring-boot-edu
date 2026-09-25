#!/usr/bin/env bash
# Creates the local Kubernetes cluster "bookstore" with kind (modules 22, 23 and the capstone) and installs the
# metrics-server, which the HorizontalPodAutoscaler needs. Safe to run again: an existing cluster is kept.
#
# Needs: Docker, kind, kubectl.   Remove the cluster with scripts/kind-down.sh
set -euo pipefail

CLUSTER=bookstore
METRICS_SERVER=https://github.com/kubernetes-sigs/metrics-server/releases/download/v0.8.0/components.yaml

if kind get clusters | grep -qx "$CLUSTER"; then
  echo "Cluster '$CLUSTER' exists."
else
  kind create cluster --name "$CLUSTER" --wait 120s
fi
kubectl config use-context "kind-$CLUSTER" > /dev/null

# kind's kubelets use self-signed certificates: metrics-server must accept them
kubectl apply -f "$METRICS_SERVER" > /dev/null
kubectl -n kube-system patch deployment metrics-server --type=json \
  -p '[{"op":"add","path":"/spec/template/spec/containers/0/args/-","value":"--kubelet-insecure-tls"}]' > /dev/null 2>&1 || true
kubectl -n kube-system rollout status deployment/metrics-server --timeout=180s

kubectl get nodes
