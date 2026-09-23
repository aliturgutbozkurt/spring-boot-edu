package com.springbootedu.hazelcast.exercise1;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
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

        // TODO 1a: a near cache for the "books" map only, keeping deserialized objects
        // TODO 1b: local copies expire after 60 seconds and are invalidated when the cluster changes the entry
        // TODO 1c: add the near cache to the client configuration

        return HazelcastClient.newHazelcastClient(config);
    }
}
