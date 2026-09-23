package com.springbootedu.hazelcast.cluster;

import static org.assertj.core.api.Assertions.assertThat;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Lesson 3.7 — two members find each other, split the data and keep a backup of each other's partitions.
 */
class TwoMemberClusterTest {

    @AfterEach
    void shutdown() {
        Hazelcast.shutdownAll();
    }

    // tag::two-members[]
    private static Config memberConfig() {
        Config config = new Config();
        config.setClusterName("cluster-demo");                                 // only members with this name join
        config.setProperty("hazelcast.phone.home.enabled", "false");
        var join = config.getNetworkConfig().getJoin();
        join.getAutoDetectionConfig().setEnabled(false);
        join.getMulticastConfig().setEnabled(false);
        join.getTcpIpConfig().setEnabled(true).addMember("127.0.0.1");       // find each other on this machine
        config.getMapConfig("books").setBackupCount(1);                       // one backup copy on another member
        return config;
    }
    // end::two-members[]

    @Test
    void twoMembersFormOneCluster() {
        HazelcastInstance first = Hazelcast.newHazelcastInstance(memberConfig());
        HazelcastInstance second = Hazelcast.newHazelcastInstance(memberConfig());

        assertThat(first.getCluster().getMembers()).hasSize(2);
        assertThat(second.getCluster().getMembers()).hasSize(2);
    }

    @Test
    void dataWrittenOnOneMemberIsReadableOnTheOther() {
        HazelcastInstance first = Hazelcast.newHazelcastInstance(memberConfig());
        HazelcastInstance second = Hazelcast.newHazelcastInstance(memberConfig());

        first.getMap("books").put("9780134685991", "Effective Java");

        assertThat(second.getMap("books").get("9780134685991")).isEqualTo("Effective Java");
    }

    @Test
    void theDataSurvivesWhenOneMemberLeaves() {
        HazelcastInstance first = Hazelcast.newHazelcastInstance(memberConfig());
        HazelcastInstance second = Hazelcast.newHazelcastInstance(memberConfig());
        IMap<Integer, String> books = first.getMap("books");
        for (int i = 0; i < 100; i++) {
            books.put(i, "book-" + i);                          // spread over the partitions of both members
        }

        first.shutdown();

        assertThat(second.getMap("books").size()).isEqualTo(100);     // backups were promoted to owners
    }
}
