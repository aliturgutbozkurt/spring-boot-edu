# Lesson 3.6 — one Dockerfile for all services of this module (the one from module 20); compose.yaml builds
# each service with its own folder as context. up.sh builds the jars first.

# --- Stage 1: a full JDK 27 to take the jar apart and to build a runtime
FROM amazoncorretto:27-alpine AS builder
# jlink --strip-debug calls objcopy, which is part of binutils
RUN apk add --no-cache binutils
WORKDIR /builder
COPY target/*-SNAPSHOT.jar application.jar
# one folder per layer: dependencies change rarely, the application often
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted
# which JDK modules does the application really use?
RUN jdeps --ignore-missing-deps --print-module-deps --recursive --multi-release 27 \
        --class-path 'extracted/dependencies/lib/*' extracted/application/application.jar > modules.txt
# a JRE with only these modules
RUN jlink --add-modules "$(cat modules.txt)" \
        --strip-debug --no-man-pages --no-header-files --compress=zip-6 --output /jre

# --- Stage 2: only the JRE and the application, on plain Alpine
FROM alpine:3.22
RUN addgroup -S spring && adduser -S spring -G spring
ENV JAVA_HOME=/opt/jre PATH="/opt/jre/bin:${PATH}"
COPY --from=builder /jre /opt/jre
WORKDIR /application
# the least changing layer first: a new build of the application reuses the cached dependency layer
COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./
USER spring
EXPOSE 8080
HEALTHCHECK --interval=10s --timeout=3s --start-period=30s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health/liveness || exit 1
# exec form: java is PID 1 and receives SIGTERM from "docker stop" → graceful shutdown
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "application.jar"]
