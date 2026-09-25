#!/usr/bin/env bash
# Lesson 3.2 — builds the same application three ways and compares the image sizes.
#
# Needs: JDK 27 (export JAVA_HOME=$(/usr/libexec/java_home -v 27)) and Docker.
# Usage: modules/20-docker-deployment/image-sizes.sh
set -euo pipefail

cd "$(dirname "$0")/../.."                                   # repository root
MODULE=modules/20-docker-deployment/lesson
REPO=springbootedu/docker-deployment

./mvnw -q -pl "$MODULE" package -DskipTests

echo "1/3 naive Dockerfile (full JDK, fat jar, root) ..."
docker build -q -f "$MODULE/Dockerfile.simple" -t "$REPO:simple" "$MODULE" > /dev/null
echo "2/3 multi-stage Dockerfile (layers, jlink JRE, non-root) ..."
docker build -q -t "$REPO:jlink" "$MODULE" > /dev/null
echo "3/3 Buildpacks (spring-boot:build-image) ..."
./mvnw -q -pl "$MODULE" spring-boot:build-image -DskipTests > /dev/null

echo
printf '| %-10s | %8s |\n' "Image" "Size"
echo "|------------|----------|"
for tag in simple jlink buildpacks; do
  printf '| %-10s | %8s |\n' "$tag" "$(docker image inspect "$REPO:$tag" --format '{{.Size}}' | awk '{printf "%.0f MB", $1 / 1000000}')"
done
