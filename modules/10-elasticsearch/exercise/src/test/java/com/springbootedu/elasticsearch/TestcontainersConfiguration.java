package com.springbootedu.elasticsearch;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Given: one real Elasticsearch for all tests (same image as compose.yaml).
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    static final ElasticsearchContainer ELASTICSEARCH = new ElasticsearchContainer(
            DockerImageName.parse("elasticsearch:9.4.5")
                    .asCompatibleSubstituteFor("docker.elastic.co/elasticsearch/elasticsearch"))
            .withEnv("xpack.security.enabled", "false")
            .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m");

    @Bean
    @ServiceConnection
    ElasticsearchContainer elasticsearchContainer() {
        return ELASTICSEARCH;
    }
}
