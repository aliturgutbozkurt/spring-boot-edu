package com.springbootedu.capstone.search;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * One Elasticsearch and one Redis per JVM for all search tests.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    static final KafkaContainer KAFKA = new KafkaContainer("apache/kafka:4.2.1");

    static final ElasticsearchContainer ELASTICSEARCH = new ElasticsearchContainer(
            DockerImageName.parse("elasticsearch:9.4.5")
                    .asCompatibleSubstituteFor("docker.elastic.co/elasticsearch/elasticsearch"))
            .withEnv("xpack.security.enabled", "false")
            .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m");

    static final RedisContainer REDIS = new RedisContainer(DockerImageName.parse("redis:8.8.3-alpine"));

    @Bean
    @ServiceConnection
    ElasticsearchContainer elasticsearchContainer() {
        return ELASTICSEARCH;
    }

    @Bean
    @ServiceConnection
    RedisContainer redisContainer() {
        return REDIS;
    }

    @Bean
    @ServiceConnection
    KafkaContainer kafkaContainer() {
        return KAFKA;
    }
}
