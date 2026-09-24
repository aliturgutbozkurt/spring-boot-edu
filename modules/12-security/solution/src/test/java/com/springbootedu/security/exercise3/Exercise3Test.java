package com.springbootedu.security.exercise3;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

/**
 * Exercise 3 — an identity provider puts roles into its own claim; map them to Spring Security roles.
 */
@SpringBootTest
@AutoConfigureMockMvc
class Exercise3Test {

    @Autowired
    MockMvcTester mvc;

    @Autowired
    JwtEncoder tokens;

    @Test
    void theRolesClaimBecomesSpringSecurityRoles() {
        String admin = token("user-42", "grace", List.of("admin"));
        String customer = token("user-7", "ada", List.of("customer"));

        assertThat(mvc.get().uri("/api/admin/report").header(HttpHeaders.AUTHORIZATION, "Bearer " + admin))
                .hasStatusOk();
        assertThat(mvc.get().uri("/api/admin/report").header(HttpHeaders.AUTHORIZATION, "Bearer " + customer))
                .hasStatus(403);
        assertThat(mvc.get().uri("/api/cart").header(HttpHeaders.AUTHORIZATION, "Bearer " + customer))
                .hasStatusOk();
    }

    @Test
    void theUserNameComesFromThePreferredUsernameClaim() {
        String customer = token("user-7", "ada", List.of("customer"));

        assertThat(mvc.get().uri("/api/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + customer))
                .hasStatusOk()
                .bodyJson().extractingPath("$.name").isEqualTo("ada");        // not the technical "user-7"
    }

    @Test
    void theAuthoritiesContainTheMappedRoles() {
        String admin = token("user-42", "grace", List.of("admin", "customer"));

        assertThat(mvc.get().uri("/api/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + admin))
                .bodyJson().extractingPath("$.authorities").asArray()
                .contains("ROLE_ADMIN", "ROLE_CUSTOMER");
    }

    private String token(String subject, String username, List<String> roles) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("https://id.bookstore.example")
                .subject(subject)
                .claim("preferred_username", username)
                .claim("roles", roles)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .build();
        return tokens.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
