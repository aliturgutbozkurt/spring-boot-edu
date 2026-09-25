# ADR-8 — one image recipe for all four services (module 20): layered jar, a jlink JRE, a non-root user.
# Build context = the repository root (compose.yaml sets it):
#   docker build -f capstone/Dockerfile --build-arg SERVICE=order-service .
# JAR_SOURCE=source (default): Maven builds the jar inside Docker — "docker compose up --build" on a clean clone.
# JAR_SOURCE=host: the jar from capstone/<service>/target (faster; up.sh, deploy-kind.sh and the e2e test).
ARG JAR_SOURCE=source

# --- Option 1: build the jar from source (glibc image: the protobuf compiler does not run on Alpine/musl)
FROM amazoncorretto:27 AS jar-from-source
ARG SERVICE
# the Maven wrapper unpacks Maven with tar and gzip, which this image does not include
RUN dnf install -y -q tar gzip && dnf clean all
WORKDIR /src
COPY mvnw ./
COPY .mvn/wrapper/maven-wrapper.properties .mvn/wrapper/
COPY build-parent/pom.xml build-parent/
COPY capstone capstone
# the cache mount keeps ~/.m2 between builds: dependencies are downloaded once for all four services
RUN --mount=type=cache,target=/root/.m2,sharing=locked \
    ./mvnw -q -B -f capstone/pom.xml -pl "$SERVICE" -am package -DskipTests \
    && cp capstone/"$SERVICE"/target/*-SNAPSHOT.jar /application.jar

# --- Option 2: the jar the host has built
FROM scratch AS jar-from-host
ARG SERVICE
COPY capstone/${SERVICE}/target/*-SNAPSHOT.jar /application.jar

FROM jar-from-${JAR_SOURCE} AS jar

# --- Stage 1: a full JDK 27 to take the jar apart and to build a runtime
FROM amazoncorretto:27-alpine AS builder
RUN apk add --no-cache binutils
WORKDIR /builder
COPY --from=jar /application.jar application.jar
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
