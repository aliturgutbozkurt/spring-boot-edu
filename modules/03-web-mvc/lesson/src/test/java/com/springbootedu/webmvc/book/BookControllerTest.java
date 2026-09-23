package com.springbootedu.webmvc.book;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.webmvc.error.ApiExceptionHandler;
import com.springbootedu.webmvc.json.IsbnJacksonComponent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

/**
 * Lessons 3.1–3.7 — a web slice: only MVC infrastructure, our controller, advice and Jackson components.
 * Tests that change the in-memory data are marked @DirtiesContext, so every test starts from the same 5 books.
 */
@WebMvcTest(BookController.class)
@Import({BookService.class, InMemoryBookRepository.class, ApiExceptionHandler.class, IsbnJacksonComponent.class})
class BookControllerTest {

    @Autowired
    MockMvcTester mvc;

    // ---- 3.1 CRUD ----------------------------------------------------------------------------

    // tag::mockmvctester[]
    @Test
    void getsABook() {
        assertThat(mvc.get().uri("/api/books/1"))
                .hasStatusOk()
                .bodyJson().isLenientlyEqualTo("""
                        {"id": 1, "isbn": "9780134685991", "title": "Effective Java", "author": "Joshua Bloch",
                         "price": 89.90, "publishedOn": "2018-01-06"}""");
    }
    // end::mockmvctester[]

    @Test
    @DirtiesContext
    void createsABookAndPointsToIt() {
        assertThat(mvc.post().uri("/api/books").contentType(MediaType.APPLICATION_JSON).content("""
                {"isbn": "978-0-13-235088-4", "title": "Clean Code", "authors": ["Robert C. Martin"],
                 "price": 75.50, "publishedOn": "2008-08-01"}"""))
                .hasStatus(HttpStatus.CREATED)
                .hasHeader("Location", "http://localhost/api/books/6")
                .bodyJson().isLenientlyEqualTo("""
                        {"id": 6, "isbn": "9780132350884", "title": "Clean Code"}""");
    }

    @Test
    @DirtiesContext
    void updatesABook() {
        assertThat(mvc.put().uri("/api/books/2").contentType(MediaType.APPLICATION_JSON).content("""
                {"isbn": "9780321336781", "title": "Java Puzzlers (2nd print)", "authors": ["Joshua Bloch", "Neal Gafter"],
                 "price": 60.00, "publishedOn": "2005-07-04"}"""))
                .hasStatusOk()
                .bodyJson().extractingPath("$.title").isEqualTo("Java Puzzlers (2nd print)");
    }

    @Test
    @DirtiesContext
    void deletesABook() {
        assertThat(mvc.delete().uri("/api/books/3")).hasStatus(HttpStatus.NO_CONTENT);
        assertThat(mvc.get().uri("/api/books/3")).hasStatus(HttpStatus.NOT_FOUND);
    }

    // ---- 3.2 validation + 3.3 ProblemDetail -----------------------------------------------------

    @Test
    void invalidInputIsA400ProblemWithFieldErrors() {
        assertThat(mvc.post().uri("/api/books").contentType(MediaType.APPLICATION_JSON).content("""
                {"isbn": "123", "title": "", "authors": [], "price": -5}"""))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON)
                .bodyJson().isLenientlyEqualTo("""
                        {"title": "Invalid request", "status": 400}""")
                .extractingPath("$.errors[*].field").asArray()
                .containsExactlyInAnyOrder("isbn", "title", "authors", "price", "publishedOn");
    }

    @Test
    void anUnknownBookIsA404Problem() {
        assertThat(mvc.get().uri("/api/books/99"))
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON)
                .bodyJson().isLenientlyEqualTo("""
                        {"type": "https://springbootedu.com/problems/book-not-found",
                         "title": "Book not found", "status": 404,
                         "detail": "No book with id 99", "instance": "/api/books/99", "bookId": 99}""");
    }

    @Test
    void anInvalidPathVariableIsA400Problem() {
        assertThat(mvc.get().uri("/api/books/0")).hasStatus(HttpStatus.BAD_REQUEST)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON);
    }

    // ---- 3.4 paging & sorting --------------------------------------------------------------------

    @Test
    void pagesAndSorts() {
        assertThat(mvc.get().uri("/api/books?page=1&size=2&sort=price&direction=desc"))
                .hasStatusOk()
                .bodyJson().isLenientlyEqualTo("""
                        {"page": 1, "size": 2, "totalElements": 5, "totalPages": 3}""")
                .extractingPath("$.content[*].title").asArray()
                .containsExactly("Effective Java", "Refactoring");
    }

    @Test
    void rejectsTooLargePages() {
        assertThat(mvc.get().uri("/api/books?size=500")).hasStatus(HttpStatus.BAD_REQUEST);
    }

    // ---- 3.5 API versioning ------------------------------------------------------------------

    @Test
    void version2HasAMoneyObjectAndAnAuthorList() {
        assertThat(mvc.get().uri("/api/books/2").header("API-Version", "2"))
                .hasStatusOk()
                .bodyJson().isLenientlyEqualTo("""
                        {"id": 2, "authors": ["Joshua Bloch", "Neal Gafter"],
                         "price": {"amount": 55.00, "currency": "TRY"}}""");
    }

    @Test
    void withoutAHeaderTheDefaultVersion1IsUsed() {
        assertThat(mvc.get().uri("/api/books/2"))
                .bodyJson().extractingPath("$.author").isEqualTo("Joshua Bloch, Neal Gafter");
    }

    @Test
    void anUnsupportedVersionIsRejected() {
        assertThat(mvc.get().uri("/api/books/2").header("API-Version", "7")).hasStatus(HttpStatus.BAD_REQUEST);
    }

    // ---- 3.6 content negotiation -------------------------------------------------------------

    @Test
    void theSameResourceAsCsv() {
        assertThat(mvc.get().uri("/api/books").accept("text/csv"))
                .hasStatusOk()
                .hasContentTypeCompatibleWith("text/csv")
                .bodyText().startsWith("id,isbn,title,price\n1,9780134685991,Effective Java,89.90\n");
    }
}
