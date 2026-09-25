package com.springbootedu.springai.stock;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.springai.AiTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Lesson 3.5 — a tool is a normal method: test it like one.
 */
@AiTest
class StockToolsTest {

    @Autowired
    StockTools tools;

    @Test
    void theToolReadsTheStock() {
        assertThat(tools.stockOf("9780134685991")).isEqualTo(12);
        assertThat(tools.stockOf("9781617297571")).isZero();
        assertThat(tools.stockOf("0000000000000")).isZero();
    }
}
