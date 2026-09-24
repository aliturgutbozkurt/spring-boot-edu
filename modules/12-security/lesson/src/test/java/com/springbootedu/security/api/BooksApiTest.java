package com.springbootedu.security.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import com.springbootedu.security.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

/**
 * Lessons 3.3–3.5 and 3.8 — the API chain: stateless, HTTP Basic or a JWT, no CSRF, CORS for one origin.
 */
@SpringBootTest(properties = "bookstore.tour.enabled=false")
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class BooksApiTest {

    private static final String NEW_BOOK = """
            {"isbn": "9780000000017", "title": "Secure by Design"}""";

    @Autowired
    MockMvcTester mvc;

    @Test
    void readingBooksIsPublic() {
        assertThat(mvc.get().uri("/api/books")).hasStatusOk();
    }

    @Test
    void writingWithoutCredentialsIs401WithAChallenge() {
        assertThat(post()).hasStatus(401).headers().containsHeader(HttpHeaders.WWW_AUTHENTICATE);
    }

    @Test
    void aCustomerIsAuthenticatedButNotAllowedToWrite() {
        assertThat(post().with(httpBasic("ada", "ada-password"))).hasStatus(403);
    }

    @Test
    void anAdminMayWrite() {
        assertThat(post().with(httpBasic("admin", "admin-password"))).hasStatus(201);
    }

    @Test
    void aJwtWithTheWriteScopeMayWrite() {
        assertThat(post().with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_books.write")))).hasStatus(201);
        assertThat(post().with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_books.read")))).hasStatus(403);
    }

    @Test
    void theApiNeedsNoCsrfTokenBecauseItHasNoSession() {
        assertThat(post().with(httpBasic("admin", "admin-password")))
                .hasStatus(201).headers().doesNotContainHeader(HttpHeaders.SET_COOKIE);
    }

    @Test
    void theAllowedOriginPassesTheCorsPreflight() {
        assertThat(mvc.options().uri("/api/books")
                .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .hasStatusOk()
                .headers().hasValue(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173");
    }

    @Test
    void anotherOriginIsRejected() {
        assertThat(mvc.options().uri("/api/books")
                .header(HttpHeaders.ORIGIN, "https://evil.example")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .hasStatus(403);
    }

    private MockMvcTester.MockMvcRequestBuilder post() {
        return mvc.post().uri("/api/books").contentType(MediaType.APPLICATION_JSON).content(NEW_BOOK);
    }
}
