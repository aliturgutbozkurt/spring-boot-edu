package com.springbootedu.asyncschedulingbatch;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * The whole application starts, and the lesson tour runs (async calls and one batch job).
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class AsyncSchedulingBatchApplicationTest {

    @Test
    void contextLoads() {
    }
}
