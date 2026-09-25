# ADR-8 — one image recipe for all four services (module 20): layered jar, a jlink JRE, a non-root user.
# The jar must be built first: capstone/up.sh does it, or  ./mvnw -pl capstone/<service> -am package -DskipTests
# Build context = the service folder, e.g.  docker build -f capstone/Dockerfile capstone/order-service

# --- Stage 1: a full JDK 27 to take the jar apart and to build a runtime
FROM amazoncorretto:27-alpine AS builder
RUN apk add --no-cache binutils
WORKDIR /builder
COPY target/*-SNAPSHOT.jar application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted
# the JDK modules the service uses, plus two that jdeps cannot see (reflection: JMX metrics, Netty's Unsafe)
RUN jdeps --ignore-missing-deps --print-module-deps --recursive --multi-release 27 \
        --class-path 'extracted/dependencies/lib/*' extracted/application/application.jar > modules.txt \
    && echo ",jdk.management,jdk.unsupported" >> modules.txt
RUN jlink --add-modules "$(tr -d '\n' < modules.txt)" \
        --strip-debug --no-man-pages --no-header-files --compress=zip-6 --output /jre

# --- Stage 2: only the JRE and the application, on plain Alpine
FROM alpine:3.22
RUN addgroup -S spring && adduser -S spring -G spring
ENV JAVA_HOME=/opt/jre PATH="/opt/jre/bin:${PATH}" PORT=8080
COPY --from=builder /jre /opt/jre
WORKDIR /application
COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./
USER spring
EXPOSE 8080
HEALTHCHECK --interval=10s --timeout=3s --start-period=60s --retries=5 \
    CMD wget -qO- http://localhost:8080/actuator/health/readiness || exit 1
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "application.jar"]
