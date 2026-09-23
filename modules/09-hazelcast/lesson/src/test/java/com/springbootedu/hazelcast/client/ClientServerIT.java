package com.springbootedu.hazelcast.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.hazelcast.client.Client;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.springbootedu.hazelcast.catalog.Book;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;

/**
 * Lesson 3.6 — client-server: the application is only a client; the data lives in a Hazelcast member in Docker.
 */
@SpringBootTest(properties = "bookstore.tour.enabled=false")
@ActiveProfiles("client")
@Import(ClientServerIT.HazelcastServer.class)
class ClientServerIT {

    // tag::testcontainers[]
    @TestConfiguration(proxyBeanMethods = false)
    static class HazelcastServer {

        static final GenericContainer<?> MEMBER = new GenericContainer<>("hazelcast/hazelcast:5.5.0")  // as compose.yaml
                .withEnv("HZ_CLUSTERNAME", "bookstore")             // Boot reads it to set the client's cluster name
                .withExposedPorts(5701);

        @Bean
        @ServiceConnection(name = "hazelcast/hazelcast")         // generic container: tell Boot what it runs
        GenericContainer<?> hazelcastMember() {
            return MEMBER;
        }
    }
    // end::testcontainers[]

    @Autowired
    HazelcastInstance hazelcast;

    @Test
    void theApplicationIsAClientNotAMember() {
        assertThat(hazelcast.getLocalEndpoint()).isInstanceOf(Client.class);      // a member would be a Member
    }

    @Test
    void recordsTravelToTheServerWithoutSharingClasses() {
        IMap<String, Book> books = hazelcast.getMap("books");
        books.put("9780134685991", new Book("9780134685991", "Effective Java", new BigDecimal("89.90")));

        assertThat(books.get("9780134685991")).isNotNull()
                .extracting(Book::title).isEqualTo("Effective Java");
    }

    @Test
    void theNearCacheServesRepeatedReadsLocally() {
        IMap<String, Book> books = hazelcast.getMap("books");
        books.put("9781617297571", new Book("9781617297571", "Spring in Action", new BigDecimal("95.00")));

        for (int i = 0; i < 5; i++) {
            books.get("9781617297571");                           // 1st: from the server, then from the near cache
        }

        assertThat(books.getLocalMapStats().getNearCacheStats().getHits()).isGreaterThanOrEqualTo(4);
    }
}
