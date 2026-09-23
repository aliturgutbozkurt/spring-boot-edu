package com.springbootedu.rediscaching;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.utility.DockerImageName;

/**
 * Lesson 3.8 — one real Redis for all tests (same image as compose.yaml).
 */
// tag::testcontainers[]
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    static final RedisContainer REDIS = new RedisContainer(DockerImageName.parse("redis:8.8.3-alpine"));

    @Bean
    @ServiceConnection
    RedisContainer redisContainer() {                       // not "redis…Template/ConnectionFactory": avoid Boot's bean names
        return REDIS;
    }
}
// end::testcontainers[]
