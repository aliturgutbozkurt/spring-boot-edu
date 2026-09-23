package com.springbootedu.hazelcast.exercise1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.springbootedu.hazelcast.TestMembers;
import java.time.Duration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Exercise 1 — a client with a near cache: repeated reads stay local, changes still arrive.
 */
class Exercise1Test {

    private static final String CLUSTER = "exercise1";

    static HazelcastInstance member;
    static HazelcastInstance client;

    @BeforeAll
    static void start() {
        member = TestMembers.start(CLUSTER);
        client = NearCacheClient.create(TestMembers.addressOf(member), CLUSTER);
    }

    @AfterAll
    static void stop() {
        client.shutdown();
        member.shutdown();
    }

    @BeforeEach
    void seed() {
        member.getMap("books").clear();
        member.<String, String>getMap("books").put("9780134685991", "Effective Java");
    }

    @Test
    void repeatedReadsAreServedByTheNearCache() {
        IMap<String, String> books = client.getMap("books");
        long hitsBefore = books.getLocalMapStats().getNearCacheStats().getHits();

        for (int i = 0; i < 10; i++) {
            books.get("9780134685991");
        }

        assertThat(books.getLocalMapStats().getNearCacheStats().getHits() - hitsBefore).isGreaterThanOrEqualTo(9);
    }

    @Test
    void aChangeOnTheClusterReachesTheClient() {
        IMap<String, String> books = client.getMap("books");
        books.get("9780134685991");                                     // now in the near cache

        member.<String, String>getMap("books").put("9780134685991", "Effective Java, 3rd Edition");

        await().atMost(Duration.ofSeconds(5))
                .until(() -> "Effective Java, 3rd Edition".equals(books.get("9780134685991")));
    }

    @Test
    void otherMapsHaveNoNearCache() {
        assertThat(client.getMap("orders").getLocalMapStats().getNearCacheStats()).isNull();
    }

    @Test
    void theNearCacheIsFasterThanGoingToTheMember() {
        ReadTimer.Result result = ReadTimer.compare(client, "books", "orders", "9780134685991", 2_000);

        System.out.println(result);                                     // look at the numbers yourself
        assertThat(result.nearCachedNanosPerRead()).isLessThan(result.remoteNanosPerRead());
    }
}
