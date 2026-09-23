package com.springbootedu.hazelcast.client;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.boot.hazelcast.autoconfigure.HazelcastConnectionDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Lesson 3.6 — the application as a client of a Hazelcast cluster, with a near cache for the "books" map.
 */
// tag::client-config[]
@Configuration(proxyBeanMethods = false)
@Profile("client")
public class ClientConfiguration {

    @Bean(destroyMethod = "shutdown")
    HazelcastInstance hazelcastInstance(HazelcastConnectionDetails connection) {
        ClientConfig config = connection.getClientConfig();          // address + cluster name from Docker Compose / Testcontainers
        config.addNearCacheConfig(new NearCacheConfig("books")
                .setInMemoryFormat(InMemoryFormat.OBJECT)             // keep deserialized objects: fastest reads
                .setTimeToLiveSeconds(60)                             // bound how stale a local copy can get
                .setInvalidateOnChange(true));                        // the cluster tells us when an entry changes
        return HazelcastClient.newHazelcastClient(config);
    }
}
// end::client-config[]
