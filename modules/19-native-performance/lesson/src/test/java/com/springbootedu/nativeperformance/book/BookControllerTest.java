package com.springbootedu.nativeperformance.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.springbootedu.nativeperformance.price.PriceFormats;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

/**
 * Lesson 3.1 — a small REST API: the application that we build as JVM, AOT and native image.
 */
@WebMvcTest(BookController.class)
class BookControllerTest {

    private static final Book EFFECTIVE_JAVA = new Book("9780134685991", "Effective Java", new BigDecimal("89.90"), 2018);

    @Autowired
    MockMvcTester mvc;

    @MockitoBean
    BookRepository books;

    @MockitoBean
    PriceFormats priceFormats;

    @Test
    void searchByTitle() {
        given(books.findByTitleContainingIgnoreCaseOrderByTitle("java")).willReturn(List.of(EFFECTIVE_JAVA));

        assertThat(mvc.get().uri("/api/books?title=java"))
                .hasStatusOk()
                .bodyJson().extractingPath("$[0].title").isEqualTo("Effective Java");
    }

    @Test
    void theFormattedPrice() {
        given(books.findById("9780134685991")).willReturn(Optional.of(EFFECTIVE_JAVA));
        given(priceFormats.format(new BigDecimal("89.90"))).willReturn("₺89,90");

        assertThat(mvc.get().uri("/api/books/9780134685991/price"))
                .hasStatusOk()
                .bodyText().isEqualTo("₺89,90");
    }

    @Test
    void anUnknownBookIsAProblemDetail() {
        given(books.findById("0000000000000")).willReturn(Optional.empty());

        assertThat(mvc.get().uri("/api/books/0000000000000"))
                .hasStatus(HttpStatus.NOT_FOUND)
                .bodyJson().extractingPath("$.title").isEqualTo("Not Found");
    }
}
