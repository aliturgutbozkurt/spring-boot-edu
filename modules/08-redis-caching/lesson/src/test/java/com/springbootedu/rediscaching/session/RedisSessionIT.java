package com.springbootedu.rediscaching.session;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.rediscaching.TestcontainersConfiguration;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * Lesson 3.7 — Spring Session keeps the HTTP session in Redis: any instance of the app can serve the next request.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "bookstore.tour.enabled=false")
@AutoConfigureRestTestClient
@Import(TestcontainersConfiguration.class)
class RedisSessionIT {

    @Autowired
    RestTestClient client;

    @Autowired
    StringRedisTemplate redis;

    @Test
    void theSessionLivesInRedis() {
        var first = client.get().uri("/api/visits").exchange()
                .expectStatus().isOk()
                .expectBody(String.class).returnResult();
        String cookie = Objects.requireNonNull(first.getResponseHeaders().getFirst(HttpHeaders.SET_COOKIE)).split(";")[0];

        assertThat(first.getResponseBody()).isEqualTo("visits in this session: 1");
        client.get().uri("/api/visits").header(HttpHeaders.COOKIE, cookie).exchange()
                .expectBody(String.class).isEqualTo("visits in this session: 2");

        String sessionId = new String(java.util.Base64.getDecoder().decode(cookie.substring("SESSION=".length())));
        assertThat(redis.hasKey("spring:session:sessions:" + sessionId)).isTrue();
    }
}
