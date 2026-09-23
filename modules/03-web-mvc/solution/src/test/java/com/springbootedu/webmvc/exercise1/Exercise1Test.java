package com.springbootedu.webmvc.exercise1;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

/**
 * Exercise 1 — an author endpoint with validation.
 */
@WebMvcTest(AuthorController.class)
@Import(AuthorRepository.class)
class Exercise1Test {

    @Autowired
    MockMvcTester mvc;

    @Test
    void listsAuthorsSortedByName() {
        assertThat(mvc.get().uri("/api/authors"))
                .hasStatusOk()
                .bodyJson().extractingPath("$[*].name").asArray()
                .containsExactly("Joshua Bloch", "Oğuz Atay", "Sabahattin Ali");
    }

    @Test
    void getsOneAuthor() {
        assertThat(mvc.get().uri("/api/authors/2"))
                .hasStatusOk()
                .bodyJson().isLenientlyEqualTo("""
                        {"id": 2, "name": "Sabahattin Ali", "country": "TR"}""");
    }

    @Test
    void anUnknownAuthorIsA404() {
        assertThat(mvc.get().uri("/api/authors/42")).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void createsAnAuthor() {
        assertThat(mvc.post().uri("/api/authors").contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "Orhan Pamuk", "country": "TR"}"""))
                .hasStatus(HttpStatus.CREATED)
                .hasHeader("Location", "http://localhost/api/authors/4")
                .bodyJson().isLenientlyEqualTo("""
                        {"id": 4, "name": "Orhan Pamuk", "country": "TR"}""");
    }

    @Test
    void rejectsABlankNameAndAnInvalidCountryCode() {
        assertThat(mvc.post().uri("/api/authors").contentType(MediaType.APPLICATION_JSON).content("""
                {"name": " ", "country": "TUR"}"""))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }
}
