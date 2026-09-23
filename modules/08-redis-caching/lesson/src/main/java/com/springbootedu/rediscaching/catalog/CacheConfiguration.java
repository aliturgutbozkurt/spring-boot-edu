package com.springbootedu.rediscaching.catalog;

import java.time.Duration;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

/**
 * Lesson 3.2 — switches caching on and configures the "books" cache in Redis.
 */
// tag::cache-config[]
@Configuration(proxyBeanMethods = false)
@EnableCaching
public class CacheConfiguration {

    @Bean
    RedisCacheManagerBuilderCustomizer booksCache() {
        return builder -> builder.withCacheConfiguration("books", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))                                 // stale data expires on its own
                .serializeValuesWith(SerializationPair.fromSerializer(
                        new JacksonJsonRedisSerializer<>(Book.class)))            // readable JSON, typed to Book
                .disableCachingNullValues());
    }
}
// end::cache-config[]
