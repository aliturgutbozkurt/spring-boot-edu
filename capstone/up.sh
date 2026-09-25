#!/usr/bin/env bash
# ADR-8 — builds the four services on the host and starts the whole platform with Docker Compose.
# Gateway: http://localhost:8080   Grafana: http://localhost:3000
set -euo pipefail
cd "$(dirname "$0")/.."                                      # repository root
./mvnw -q -pl capstone/catalog-service,capstone/order-service,capstone/search-service,capstone/gateway -am \
    package -DskipTests
JAR_SOURCE=host docker compose -f capstone/compose.yaml up --build -d --wait
docker compose -f capstone/compose.yaml ps
