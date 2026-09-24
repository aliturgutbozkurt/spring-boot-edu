package com.springbootedu.security.authserver;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.security.TestcontainersConfiguration;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Lessons 3.5–3.6 — a client gets a signed JWT from our Authorization Server and calls the API with it.
 */
@SpringBootTest(properties = "bookstore.tour.enabled=false")
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class TokenFlowTest {

    @Autowired
    MockMvcTester mvc;

    @Autowired
    JsonMapper json;

    @Test
    void theClientCredentialsGrantReturnsABearerToken() {
        JsonNode token = requestToken("dev-only-secret", "books.read books.write");

        assertThat(token.get("token_type").asString()).isEqualTo("Bearer");
        assertThat(token.get("access_token").asString()).contains(".");          // header.payload.signature
        assertThat(token.get("scope").asString()).contains("books.write");
    }

    @Test
    void aWrongClientSecretIsRejected() {
        assertThat(mvc.post().uri("/oauth2/token")
                .header(HttpHeaders.AUTHORIZATION, basic("bookstore-cli", "wrong"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("grant_type=client_credentials"))
                .hasStatus(401);
    }

    @Test
    void theIssuedTokenIsAcceptedByTheApi() {
        String accessToken = requestToken("dev-only-secret", "books.read books.write").get("access_token").asString();

        assertThat(mvc.get().uri("/api/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .hasStatusOk()
                .bodyJson().extractingPath("$.name").isEqualTo("bookstore-cli");
        assertThat(mvc.post().uri("/api/books").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON).content("""
                        {"isbn": "9780000000024", "title": "OAuth 2 in Action"}"""))
                .hasStatus(201);
    }

    @Test
    void aTokenWithOnlyTheReadScopeCannotWrite() {
        String accessToken = requestToken("dev-only-secret", "books.read").get("access_token").asString();

        assertThat(mvc.post().uri("/api/books").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON).content("""
                        {"isbn": "9780000000031", "title": "Read Only"}"""))
                .hasStatus(403);
    }

    @Test
    void aTamperedTokenIsRejected() {
        String accessToken = requestToken("dev-only-secret", "books.read").get("access_token").asString();
        String tampered = accessToken.substring(0, accessToken.length() - 4) + "AAAA";   // signature no longer fits

        assertThat(mvc.get().uri("/api/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + tampered)).hasStatus(401);
    }

    @Test
    void theServerPublishesItsMetadataAndKeys() {
        assertThat(mvc.get().uri("/.well-known/oauth-authorization-server"))
                .hasStatusOk().bodyJson().extractingPath("$.token_endpoint").asString().endsWith("/oauth2/token");
        assertThat(mvc.get().uri("/oauth2/jwks"))
                .hasStatusOk().bodyJson().extractingPath("$.keys[0].kty").isEqualTo("RSA");
    }

    private JsonNode requestToken(String secret, String scopes) {
        var result = mvc.post().uri("/oauth2/token")
                .header(HttpHeaders.AUTHORIZATION, basic("bookstore-cli", secret))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("grant_type=client_credentials&scope=" + scopes.replace(" ", "+"))
                .exchange();
        assertThat(result).hasStatusOk();
        return json.readTree(result.getResponse().getContentAsByteArray());
    }

    private static String basic(String user, String password) {
        return "Basic " + Base64.getEncoder()
                .encodeToString((user + ":" + password).getBytes(StandardCharsets.UTF_8));
    }
}
