#!/usr/bin/env bash
# Lesson 3.6 — starts the same bookstore four ways and prints startup time and memory.
#
# Needs:
#   - JDK 27:      export JAVA_HOME=$(/usr/libexec/java_home -v 27)
#   - PostgreSQL:  docker compose --profile postgres up -d
#   - optional, for the native row:
#                  ./mvnw -Pnative -pl modules/19-native-performance/lesson spring-boot:build-image
#
# Usage: modules/19-native-performance/compare-startup.sh [runs per variant, default 3]
set -euo pipefail

RUNS="${1:-3}"
cd "$(dirname "$0")/../.."                                   # repository root
MODULE=modules/19-native-performance/lesson
JAR="$MODULE/target/19-native-performance-lesson-1.0.0-SNAPSHOT.jar"
WORK="$MODULE/target/startup"
IMAGE=springbootedu/native-performance:1.0.0-SNAPSHOT
PORT=8089
DB_ARGS=(--spring.docker.compose.enabled=false
         --spring.datasource.url=jdbc:postgresql://localhost:5432/bookstore
         --spring.datasource.username=bookstore --spring.datasource.password=bookstore)
JAVA="${JAVA_HOME:?set JAVA_HOME to a JDK 27}/bin/java"

if [[ ! -f "$JAR" ]]; then
  echo "Building $JAR ..."
  ./mvnw -q -pl "$MODULE" package -DskipTests
fi

# Boot recommends an extracted jar for CDS / the AOT cache (the classpath must be the same in training and use)
rm -rf "$WORK" && mkdir -p "$WORK"
"$JAVA" -Djarmode=tools -jar "$JAR" extract --destination "$WORK/app" > /dev/null
APP="$WORK/app/19-native-performance-lesson-1.0.0-SNAPSHOT.jar"

# Training run: start until the context is refreshed, then exit, and write the AOT cache (JEP 483/514)
echo "Training run for the AOT cache ..."
"$JAVA" -XX:AOTCacheOutput="$WORK/app.aot" -Dspring.aot.enabled=true -Dspring.context.exit=onRefresh \
  -jar "$APP" --server.port=$PORT "${DB_ARGS[@]}" > "$WORK/training.log" 2>&1

# Boot logs "Started ... in 0.8 seconds (process running for 1.0)". "process running" includes the JVM's own
# start, so it is the fair number to compare with a native image.
startup_ms() {
  grep -o "process running for [0-9.]*" | head -1 | awk '{print int($4 * 1000)}'
}

# Starts one JVM variant, waits until it is started, reads RSS and prints "ms MB"
measure_jvm() {
  local log="$WORK/run.log"
  "$JAVA" "$@" -jar "$APP" --server.port=$PORT "${DB_ARGS[@]}" > "$log" 2>&1 &
  local pid=$!
  for _ in $(seq 1 300); do grep -q "process running for" "$log" && break; sleep 0.1; done
  curl -sf "http://localhost:$PORT/api/books" > /dev/null             # the first request, as a user would send it
  local ms rss
  ms=$(startup_ms < "$log")
  rss=$(ps -o rss= -p "$pid" | tr -d ' ')                               # kilobytes
  kill "$pid"; wait "$pid" 2> /dev/null || true
  echo "$ms $((rss / 1024))"
}

measure_native() {
  local name=bookstore-native-startup
  docker run -d --rm --name "$name" -p $PORT:$PORT "$IMAGE" --server.port=$PORT \
    --spring.datasource.url=jdbc:postgresql://host.docker.internal:5432/bookstore \
    --spring.datasource.username=bookstore --spring.datasource.password=bookstore > /dev/null
  for _ in $(seq 1 300); do docker logs "$name" 2>&1 | grep -q "process running for" && break; sleep 0.1; done
  curl -sf "http://localhost:$PORT/api/books" > /dev/null
  local ms mem
  ms=$(docker logs "$name" 2>&1 | startup_ms)
  mem=$(docker stats --no-stream --format '{{.MemUsage}}' "$name" | awk '{print $1}')   # e.g. 85.3MiB
  docker stop "$name" > /dev/null
  echo "$ms $(printf '%.0f' "${mem%MiB}")"
}

# Runs a variant RUNS times and prints the median of each column
row() {
  local label=$1; shift
  local results=()
  for _ in $(seq 1 "$RUNS"); do results+=("$("$@")"); done
  local ms mb
  ms=$(printf '%s\n' "${results[@]}" | awk '{print $1}' | sort -n | awk '{a[NR]=$1} END {print a[int((NR+1)/2)]}')
  mb=$(printf '%s\n' "${results[@]}" | awk '{print $2}' | sort -n | awk '{a[NR]=$1} END {print a[int((NR+1)/2)]}')
  printf '| %-32s | %8s | %8s |\n' "$label" "$ms" "$mb"
}

echo
printf '| %-32s | %8s | %8s |\n' "Variant (median of $RUNS runs)" "Start ms" "RSS MB"
echo "|----------------------------------|----------|----------|"
row "JVM" measure_jvm
row "JVM + Spring AOT" measure_jvm -Dspring.aot.enabled=true
row "JVM + Spring AOT + AOT cache" measure_jvm -Dspring.aot.enabled=true -XX:AOTCache="$WORK/app.aot"
if docker image inspect "$IMAGE" > /dev/null 2>&1; then
  row "Native image (GraalVM 25)" measure_native
else
  printf '| %-32s | %8s | %8s |\n' "Native image (not built)" "-" "-"
fi
