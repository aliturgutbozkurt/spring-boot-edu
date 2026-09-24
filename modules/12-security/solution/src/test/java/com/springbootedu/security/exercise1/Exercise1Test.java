package com.springbootedu.security.exercise1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

/**
 * Exercise 1 — URL rules for two roles: CUSTOMER and ADMIN.
 */
@SpringBootTest
@AutoConfigureMockMvc
class Exercise1Test {

    @Autowired
    MockMvcTester mvc;

    @Test
    void everybodyMayReadTheCatalog() {
        assertThat(mvc.get().uri("/api/catalog")).hasStatusOk();
    }

    @Test
    void onlyAnAdminMayAddToTheCatalog() {
        assertThat(addBook().with(httpBasic("ada", "ada-password"))).hasStatus(403);
        assertThat(addBook().with(httpBasic("admin", "admin-password"))).hasStatus(201);
    }

    @Test
    void theCartIsForCustomersOnly() {
        assertThat(mvc.get().uri("/api/cart")).hasStatus(401);
        assertThat(mvc.get().uri("/api/cart").with(httpBasic("ada", "ada-password"))).hasStatusOk();
        assertThat(mvc.get().uri("/api/cart").with(httpBasic("admin", "admin-password"))).hasStatus(403);
    }

    @Test
    void theAdminAreaIsForAdminsOnly() {
        assertThat(mvc.get().uri("/api/admin/report").with(httpBasic("ada", "ada-password"))).hasStatus(403);
        assertThat(mvc.get().uri("/api/admin/report").with(httpBasic("admin", "admin-password"))).hasStatusOk();
    }

    @Test
    void everythingElseNeedsALogin() {
        assertThat(mvc.get().uri("/api/me")).hasStatus(401);
        assertThat(mvc.get().uri("/api/me").with(httpBasic("bob", "bob-password"))).hasStatusOk();
    }

    private MockMvcTester.MockMvcRequestBuilder addBook() {
        return mvc.post().uri("/api/catalog").contentType(MediaType.APPLICATION_JSON)
                .content("{\"isbn\": \"1\", \"title\": \"New\"}");
    }
}
