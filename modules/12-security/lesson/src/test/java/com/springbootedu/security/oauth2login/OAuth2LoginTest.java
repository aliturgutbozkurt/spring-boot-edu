package com.springbootedu.security.oauth2login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;

import com.springbootedu.security.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

/**
 * Lesson 3.7 — "Log in with GitHub". The test never talks to GitHub: it checks the redirect and fakes the result.
 */
@SpringBootTest(properties = {
        "bookstore.tour.enabled=false",
        "spring.security.oauth2.client.registration.github.client-id=test-client-id",
        "spring.security.oauth2.client.registration.github.client-secret=test-client-secret"})
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class OAuth2LoginTest {

    @Autowired
    MockMvcTester mvc;

    @Test
    void loginWithGithubRedirectsToGithubsAuthorizationPage() {
        assertThat(mvc.get().uri("/oauth2/authorization/github"))
                .hasStatus3xxRedirection()
                .redirectedUrl().startsWith("https://github.com/login/oauth/authorize")
                .contains("client_id=test-client-id").contains("state=");
    }

    @Test
    void aUserWhoLoggedInWithGithubSeesTheAccountPage() {
        assertThat(mvc.get().uri("/account").with(oauth2Login().attributes(a -> a.put("login", "octocat"))))
                .hasStatusOk();
    }

    @Test
    void theLoginPageOffersBothWays() {
        assertThat(mvc.get().uri("/login")).hasStatusOk().bodyText()
                .contains("/oauth2/authorization/github")               // the GitHub link
                .contains("name=\"username\"");                         // the form
    }
}
