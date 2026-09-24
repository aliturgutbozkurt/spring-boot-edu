package com.springbootedu.security.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import com.springbootedu.security.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

/**
 * Lessons 3.1 and 3.3 — the browser chain: public pages, form login with the users from PostgreSQL, CSRF.
 */
@SpringBootTest(properties = "bookstore.tour.enabled=false")
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class WebChainTest {

    @Autowired
    MockMvcTester mvc;

    @Test
    void theHomePageIsPublic() {
        assertThat(mvc.get().uri("/")).hasStatusOk();
    }

    @Test
    void aProtectedPageRedirectsToTheLoginForm() {
        assertThat(mvc.get().uri("/account")).hasStatus3xxRedirection().hasRedirectedUrl("/login");
    }

    @Test
    void theLoginFormAcceptsAUserFromTheDatabase() {
        assertThat(mvc.perform(formLogin().user("ada").password("ada-password")))
                .hasRedirectedUrl("/");
    }

    @Test
    void aWrongPasswordIsRejected() {
        assertThat(mvc.perform(formLogin().user("ada").password("wrong")))
                .hasRedirectedUrl("/login?error");
    }

    @Test
    void aLoggedInUserSeesTheAccountPage() {
        assertThat(mvc.get().uri("/account").with(user("ada").roles("CUSTOMER")))
                .hasStatusOk().bodyText().contains("ada");
    }

    @Test
    void aFormPostWithoutACsrfTokenIsForbidden() {
        assertThat(mvc.post().uri("/account/newsletter").with(user("ada").roles("CUSTOMER")))
                .hasStatus(403);
        assertThat(mvc.post().uri("/account/newsletter").with(user("ada").roles("CUSTOMER")).with(csrf()))
                .hasStatusOk();
    }
}
