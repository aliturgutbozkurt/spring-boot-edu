package com.springbootedu.hazelcast.pricing;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Lesson 3.4 — with a HazelcastInstance and hazelcast-spring on the classpath, Boot picks HazelcastCacheManager.
 */
@Configuration(proxyBeanMethods = false)
@EnableCaching
public class CachingConfiguration {
}
