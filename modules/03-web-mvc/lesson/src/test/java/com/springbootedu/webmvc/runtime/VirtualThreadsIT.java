package com.springbootedu.webmvc.runtime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * Lesson 3.9 — a real server on a random port; with spring.threads.virtual.enabled=true
 * Tomcat handles every request on a virtual thread.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class VirtualThreadsIT {

    @Autowired
    RestTestClient client;

    @Test
    void requestsRunOnVirtualThreads() {
        client.get().uri("/api/runtime/thread")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.virtual").isEqualTo(true);
    }
}
