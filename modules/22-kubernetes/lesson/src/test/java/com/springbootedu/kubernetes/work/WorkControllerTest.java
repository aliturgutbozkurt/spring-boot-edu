package com.springbootedu.kubernetes.work;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

/**
 * Lesson 3.6 — the load endpoint for the autoscaler demo.
 */
@WebMvcTest(WorkController.class)
class WorkControllerTest {

    @Autowired
    MockMvcTester mvc;

    @Test
    void itWorksForTheRequestedTime() {
        long start = System.nanoTime();

        assertThat(mvc.get().uri("/api/work?millis=100")).hasStatusOk().bodyText().startsWith("worked 100 ms");
        assertThat((System.nanoTime() - start) / 1_000_000).isGreaterThanOrEqualTo(100);
    }
}
