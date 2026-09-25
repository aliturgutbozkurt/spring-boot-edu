package com.springbootedu.capstone.catalog.book;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.capstone.catalog.CatalogTest;
import com.springbootedu.capstone.catalog.Tokens;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@CatalogTest
@AutoConfigureMockMvc
class BookApiTest {

    private static final String BOOK = """
            {"title": "Clean Code", "authors": ["Robert C. Martin"], "description": "Readable code.",
             "price": 70.00, "stock": 3}""";

    @Autowired
    MockMvcTester mvc;

    @Autowired
    BookRepository books;

    @BeforeEach
    void oneBook() {
        books.deleteAll();
        books.save(new Book("9780134685991", "Effective Java", List.of("Joshua Bloch"), "Java.", new BigDecimal("89.90"), 5, null));
    }

    @Test
    void everybodyMayReadTheCatalog() {
        assertThat(mvc.get().uri("/api/catalog/books/9780134685991")).hasStatusOk()
                .bodyJson().extractingPath("$.title").isEqualTo("Effective Java");
        assertThat(mvc.get().uri("/api/catalog/books/0000000000000")).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void onlyAnAdminMayChangeIt() {
        assertThat(mvc.put().uri("/api/catalog/books/9780132350884").contentType(MediaType.APPLICATION_JSON).content(BOOK))
                .hasStatus(HttpStatus.UNAUTHORIZED);
        assertThat(mvc.put().uri("/api/catalog/books/9780132350884").contentType(MediaType.APPLICATION_JSON).content(BOOK)
                .header("Authorization", Tokens.bearer("ayse", "USER")))
                .hasStatus(HttpStatus.FORBIDDEN);
        assertThat(mvc.put().uri("/api/catalog/books/9780132350884").contentType(MediaType.APPLICATION_JSON).content(BOOK)
                .header("Authorization", Tokens.bearer("admin", "ADMIN")))
                .hasStatusOk();

        assertThat(books.findById("9780132350884")).get().extracting(Book::stock).isEqualTo(3);
    }
}
