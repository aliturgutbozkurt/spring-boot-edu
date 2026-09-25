package com.springbootedu.grpc.exercise3;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.grpc.test.autoconfigure.AutoConfigureTestGrpcTransport;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "bookstore.latency=300ms")      // the server needs 300 ms per book
@AutoConfigureTestGrpcTransport
class Exercise3Test {

    @Autowired
    TitleLookup titles;

    @Test
    void enoughTimeGivesTheTitle() {
        assertThat(titles.titleWithin("9780134685991", Duration.ofSeconds(2))).contains("Effective Java");
    }

    @Test
    void tooLittleTimeGivesNoTitleAndDoesNotWait() {
        long start = System.nanoTime();

        assertThat(titles.titleWithin("9780134685991", Duration.ofMillis(100))).isEmpty();
        assertThat(Duration.ofNanos(System.nanoTime() - start)).isLessThan(Duration.ofMillis(250));
    }
}
