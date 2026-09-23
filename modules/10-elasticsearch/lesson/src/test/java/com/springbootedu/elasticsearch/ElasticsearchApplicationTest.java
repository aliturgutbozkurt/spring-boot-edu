package com.springbootedu.elasticsearch;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * The whole application starts, and the lesson tour runs against real PostgreSQL and Elasticsearch.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class ElasticsearchApplicationTest {

    @Test
    void contextLoads() {
    }
}
