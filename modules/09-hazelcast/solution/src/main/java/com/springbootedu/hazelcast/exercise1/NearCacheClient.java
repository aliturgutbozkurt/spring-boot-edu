package com.springbootedu.hazelcast.exercise1;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.core.HazelcastInstance;

/**
 * Exercise 1 — a Hazelcast client that keeps a local copy of the "books" map.
 */
public final class NearCacheClient {

    private NearCacheClient() {
    }

    public static HazelcastInstance create(String memberAddress, String clusterName) {
        ClientConfig config = new ClientConfig();
        config.setClusterName(clusterName);
        config.getNetworkConfig().addAddress(memberAddress);

        NearCacheConfig nearCache = new NearCacheConfig("books")
                .setInMemoryFormat(InMemoryFormat.OBJECT)
                .setTimeToLiveSeconds(60)
                .setInvalidateOnChange(true);
        config.addNearCacheConfig(nearCache);

        return HazelcastClient.newHazelcastClient(config);
    }
}
