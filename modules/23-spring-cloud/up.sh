#!/usr/bin/env bash
# Lesson 3.6 — builds the four services and starts the system with Docker Compose. The gateway: http://localhost:9000
set -euo pipefail
cd "$(dirname "$0")/../.."                                   # repository root
M=modules/23-spring-cloud
./mvnw -q -pl "$M/lesson,$M/services/catalog-service,$M/services/config-server,$M/services/gateway" package -DskipTests
docker compose -f "$M/compose.yaml" up --build -d --wait
docker compose -f "$M/compose.yaml" ps
