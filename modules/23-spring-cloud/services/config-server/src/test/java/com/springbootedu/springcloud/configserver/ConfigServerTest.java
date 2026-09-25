package com.springbootedu.springcloud.configserver;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * Lesson 3.5 — the Config Server reads committed files from a Git repository (here a temporary one).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class ConfigServerTest {

    static final Path REPOSITORY = createRepository();

    @DynamicPropertySource
    static void gitBackend(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.config.server.git.uri", () -> REPOSITORY.toUri().toString());
        registry.add("spring.cloud.config.server.git.search-paths", () -> "");
        registry.add("spring.cloud.config.server.git.basedir", ConfigServerTest::cloneDirectory);
    }

    @Autowired
    RestTestClient http;

    @Test
    void theFileOfTheApplicationIsServed() {
        http.get().uri("/order-service/default").exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("order-service")
                .jsonPath("$.propertySources[0].source['bookstore.order.max-quantity']").isEqualTo(5);
    }

    @Test
    void aProfileAddsItsOwnFile() {
        http.get().uri("/order-service/prod").exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.propertySources[0].name").value(name -> assertThat(name.toString()).contains("order-service-prod"))
                .jsonPath("$.propertySources[0].source['bookstore.order.max-quantity']").isEqualTo(20);
    }

    private static String cloneDirectory() {
        try {
            return Files.createTempDirectory("config-clone").toRealPath().toString();   // where the server clones to
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /** A Git repository with two committed files, created once for this JVM. */
    private static Path createRepository() {
        try {
            // toRealPath: on macOS /var is a symbolic link, and the Config Server refuses symbolic links
            Path directory = Files.createTempDirectory("config-repo").toRealPath();
            Files.writeString(directory.resolve("order-service.yaml"), "bookstore.order.max-quantity: 5\n");
            Files.writeString(directory.resolve("order-service-prod.yaml"), "bookstore.order.max-quantity: 20\n");
            try (Git git = Git.init().setDirectory(directory.toFile()).setInitialBranch("main").call()) {
                git.add().addFilepattern(".").call();
                git.commit().setMessage("configuration").setAuthor("test", "test@example.com").call();
            }
            return directory;
        } catch (IOException | GitAPIException e) {
            throw new IllegalStateException(e);
        }
    }
}
