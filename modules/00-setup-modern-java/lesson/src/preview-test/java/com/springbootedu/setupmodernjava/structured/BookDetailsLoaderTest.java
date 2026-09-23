package com.springbootedu.setupmodernjava.structured;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import org.junit.jupiter.api.Test;

/**
 * Lesson 3.10 — structured concurrency (preview): run with ./mvnw -Ppreview ...
 */
class BookDetailsLoaderTest {

    @Test
    void loadsPriceAndReviewsConcurrently() throws Exception {
        var loader = new BookDetailsLoader(Duration.ofMillis(300), false);
        long start = System.nanoTime();

        var details = loader.load("978-0-13-468599-1");

        assertThat(details.price()).isEqualTo("89.90");
        assertThat(details.reviews()).containsExactly("★★★★★ Harika / Great");
        assertThat(Duration.ofNanos(System.nanoTime() - start)).isLessThan(Duration.ofMillis(550));   // not 600
    }

    @Test
    void aFailingSubtaskFailsTheWholeOperation() {
        var loader = new BookDetailsLoader(Duration.ofMillis(50), true);

        assertThatThrownBy(() -> loader.load("978-0-13-468599-1"))
                .hasRootCauseInstanceOf(IllegalStateException.class)
                .hasRootCauseMessage("review service down");
    }
}
