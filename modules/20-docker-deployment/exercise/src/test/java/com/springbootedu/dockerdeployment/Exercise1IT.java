package com.springbootedu.dockerdeployment;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.time.Duration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Exercise 1 — builds exercise/Dockerfile (after "package") and checks the image.
 */
class Exercise1IT {

    static final Network NETWORK = Network.newNetwork();

    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer(
            DockerImageName.parse("pgvector/pgvector:0.8.6-pg18").asCompatibleSubstituteFor("postgres"))
            .withNetwork(NETWORK).withNetworkAliases("postgres");

    static final ImageFromDockerfile IMAGE = new ImageFromDockerfile("springbootedu/exercise1", false)
            .withFileFromPath(".", Path.of("."))                 // Dockerfile + .dockerignore + target/*.jar
            // a fresh build: cached layers of other builds of this Dockerfile may be removed while we build
            .withBuildImageCmdModifier(command -> command.withNoCache(true));

    static final GenericContainer<?> APP = new GenericContainer<>(IMAGE)
            .withNetwork(NETWORK)
            .withExposedPorts(8080)
            .withEnv("SPRING_DATASOURCE_URL", "jdbc:postgresql://postgres:5432/test")
            .withEnv("SPRING_DATASOURCE_USERNAME", "test")
            .withEnv("SPRING_DATASOURCE_PASSWORD", "test")
            .withEnv("MANAGEMENT_ENDPOINT_HEALTH_GROUP_READINESS_INCLUDE", "readinessState,db")   // no warehouse here
            .waitingFor(Wait.forHttp("/actuator/health/readiness").withStartupTimeout(Duration.ofMinutes(3)));

    @BeforeAll
    static void start() {
        POSTGRES.start();
        APP.start();
    }

    @AfterAll
    static void stop() {
        APP.stop();
        POSTGRES.stop();
    }

    @Test
    void theApplicationDoesNotRunAsRoot() throws Exception {
        String userId = APP.execInContainer("id", "-u").getStdout().trim();

        assertThat(userId).isNotEqualTo("0");
    }

    @Test
    void javaIsTheMainProcessSoItReceivesSigterm() throws Exception {
        String command = APP.execInContainer("cat", "/proc/1/cmdline").getStdout();

        assertThat(command).startsWith("java");                    // not "/bin/sh -c java ..." (shell form)
    }

    @Test
    void theImageIsSmall() {
        long bytes = DockerClientFactory.instance().client()
                .inspectImageCmd(APP.getDockerImageName()).exec().getSize();

        assertThat(bytes).isLessThan(150_000_000L);                 // the naive image is about 400 MB
    }

    @Test
    void theJavaRuntimeHasNoCompiler() throws Exception {
        var javac = APP.execInContainer("sh", "-c", "command -v javac || echo none");

        assertThat(javac.getStdout().trim()).isEqualTo("none");     // a JRE, not a JDK
    }
}
