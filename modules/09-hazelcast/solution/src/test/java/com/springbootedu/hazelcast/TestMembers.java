package com.springbootedu.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**
 * Given: starts an isolated embedded member for a test class.
 */
public final class TestMembers {

    private TestMembers() {
    }

    public static HazelcastInstance start(String clusterName) {
        Config config = new Config();
        config.setClusterName(clusterName);                         // own cluster: tests never join each other
        config.setProperty("hazelcast.phone.home.enabled", "false");
        config.setProperty("hazelcast.map.invalidation.batch.enabled", "false");   // send near cache invalidations
                                                                                  // at once, not every 10 s
        var join = config.getNetworkConfig().getJoin();
        join.getAutoDetectionConfig().setEnabled(false);
        join.getMulticastConfig().setEnabled(false);
        return Hazelcast.newHazelcastInstance(config);
    }

    public static String addressOf(HazelcastInstance member) {
        var address = member.getCluster().getLocalMember().getSocketAddress();
        return address.getHostString() + ":" + address.getPort();
    }
}
