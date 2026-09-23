package com.springbootedu.webmvc.docs;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

/**
 * Lesson 3.8 — springdoc generates an OpenAPI document from the controllers.
 */
@SpringBootTest
@AutoConfigureMockMvc
class OpenApiTest {

    @Autowired
    MockMvcTester mvc;

    @Test
    void describesTheBookApi() {
        assertThat(mvc.get().uri("/v3/api-docs"))
                .hasStatusOk()
                .bodyJson().isLenientlyEqualTo("""
                        {"info": {"title": "Kitapçı API / Bookstore API"}}""")
                .extractingPath("$.paths").asMap().containsKeys("/api/books", "/api/books/{id}");
    }

    @Test
    void servesTheSwaggerUi() {
        assertThat(mvc.get().uri("/swagger-ui/index.html")).hasStatusOk();
    }
}
