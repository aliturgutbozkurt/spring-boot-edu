package com.springbootedu.springcloud.catalogservice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(CatalogController.class)
@Import(Outage.class)
@TestPropertySource(properties = "bookstore.instance-name=catalog-1")
class CatalogControllerTest {

    @Autowired
    MockMvcTester mvc;

    @Autowired
    Outage outage;

    @AfterEach
    void healthyAgain() {
        outage.set(false, java.time.Duration.ZERO);
    }

    @Test
    void aBookSaysWhichInstanceServedIt() {
        assertThat(mvc.get().uri("/api/books/9780134685991")).hasStatusOk()
                .bodyJson().extractingPath("$.servedBy").isEqualTo("catalog-1");
    }

    @Test
    void anOutageMakesTheInstanceFail() {
        assertThat(mvc.post().uri("/admin/outage?failing=true")).hasStatusOk();

        assertThat(mvc.get().uri("/api/books/9780134685991")).hasStatus(HttpStatus.SERVICE_UNAVAILABLE);
    }
}
