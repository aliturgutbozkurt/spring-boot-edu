package com.springbootedu.dockerdeployment;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.time.Duration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.wait.strategy.Wait;

/**
 * Exercise 1 — builds exercise/Dockerfile (after "package", with exercise1-compose.yaml) and checks the image.
 */
class Exercise1IT {

    static final ComposeContainer SYSTEM = new ComposeContainer(new File("exercise1-compose.yaml"))
            .withBuild(true)
            .withExposedService("app", 8080,
                    Wait.forHttp("/actuator/health/readiness").forStatusCode(200)
                            .withStartupTimeout(Duration.ofMinutes(3)));

    @BeforeAll
    static void start() {
        SYSTEM.start();
    }

    @AfterAll
    static void stop() {
        SYSTEM.stop();
    }

    @Test
    void theApplicationDoesNotRunAsRoot() throws Exception {
        String userId = app().execInContainer("id", "-u").getStdout().trim();

        assertThat(userId).isNotEqualTo("0");
    }

    @Test
    void javaIsTheMainProcessSoItReceivesSigterm() throws Exception {
        String command = app().execInContainer("cat", "/proc/1/cmdline").getStdout();

        assertThat(command).startsWith("java");                    // not "/bin/sh -c java ..." (shell form)
    }

    @Test
    void theImageIsSmall() {
        long bytes = DockerClientFactory.instance().client()
                .inspectImageCmd("springbootedu/exercise1:latest").exec().getSize();

        assertThat(bytes).isLessThan(150_000_000L);                 // the naive image is about 400 MB
    }

    @Test
    void theJavaRuntimeHasNoCompiler() throws Exception {
        var javac = app().execInContainer("sh", "-c", "command -v javac || echo none");

        assertThat(javac.getStdout().trim()).isEqualTo("none");     // a JRE, not a JDK
    }

    private static ContainerState app() {
        return SYSTEM.getContainerByServiceName("app").orElseThrow();
    }
}
