package com.springbootedu.rediscaching.exercise1;

import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * Exercise 1 — caching on, with a JSON "product-details" cache that expires after five minutes.
 */
@Configuration(proxyBeanMethods = false)
@EnableCaching
public class CacheConfiguration {

    @Bean
    RedisCacheManagerBuilderCustomizer productDetailsCache(RedisConnectionFactory connectionFactory) {
        return builder -> builder
                .cacheWriter(RedisCacheWriter.create(connectionFactory,
                        writer -> writer.immediateWrites()));                     // given: no async write races
        // TODO 1c: configure the "product-details" cache: 5 minute TTL, JSON values typed to ProductDetail,
        //          no null values
    }
}
