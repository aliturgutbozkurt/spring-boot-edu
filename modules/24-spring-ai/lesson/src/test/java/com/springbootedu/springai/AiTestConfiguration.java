package com.springbootedu.springai;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Lesson 3.8 — the fakes instead of Ollama (see spring.ai.model.* = none in the tests), and a real pgvector.
 */
@TestConfiguration(proxyBeanMethods = false)
public class AiTestConfiguration {

    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer(
            DockerImageName.parse("pgvector/pgvector:0.8.6-pg18").asCompatibleSubstituteFor("postgres"));

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgresContainer() {
        return POSTGRES;
    }

    @Bean
    FakeChatModel fakeChatModel() {
        return new FakeChatModel();
    }

    @Bean
    KeywordEmbeddingModel keywordEmbeddingModel() {
        return new KeywordEmbeddingModel();
    }
}
