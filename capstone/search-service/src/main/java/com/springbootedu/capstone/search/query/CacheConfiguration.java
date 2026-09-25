package com.springbootedu.capstone.search.query;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

/**
 * ADR-7 — the "search" cache in Redis, shared by all search-service instances.
 */
@Configuration(proxyBeanMethods = false)
@EnableCaching
class CacheConfiguration {

    @Bean
    RedisCacheManagerBuilderCustomizer searchCache(RedisConnectionFactory connectionFactory,
                                                   @Value("${bookstore.search.cache-ttl}") Duration ttl) {
        return builder -> builder
                .cacheWriter(RedisCacheWriter.create(connectionFactory,
                        writer -> writer.immediateWrites()))              // no read-after-evict races
                .withCacheConfiguration("search", RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(ttl)
                        .serializeValuesWith(SerializationPair.fromSerializer(
                                new JacksonJsonRedisSerializer<>(SearchResult.class)))
                        .disableCachingNullValues());
    }
}
