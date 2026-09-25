package com.springbootedu.capstone.gateway.token;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.springbootedu.capstone.gateway.security.JwtSecret;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * ADR-5 — POST /api/auth/token issues a signed JWT for a demo user. Only for local development
 * (bookstore.dev-tokens.enabled in the dev profile); production uses an authorization server.
 */
@RestController
@ConditionalOnBooleanProperty("bookstore.dev-tokens.enabled")
class DevTokenController {

    private static final Duration LIFETIME = Duration.ofHours(1);

    record TokenRequest(@NotBlank String username, @NotBlank String password) {
    }

    record TokenResponse(String accessToken, String tokenType, long expiresIn) {
    }

    private final DevTokenProperties properties;
    private final JwtEncoder encoder;
    private final Clock clock = Clock.systemUTC();

    DevTokenController(DevTokenProperties properties, JwtSecret secret) {
        this.properties = properties;
        this.encoder = new NimbusJwtEncoder(new ImmutableSecret<>(secret.key()));
    }

    @PostMapping("/api/auth/token")
    TokenResponse token(@Valid @RequestBody TokenRequest request) {
        DevTokenProperties.DemoUser user = properties.users().stream()
                .filter(candidate -> candidate.username().equals(request.username()))
                .filter(candidate -> samePassword(candidate.password(), request.password()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unknown user or wrong password"));

        Instant now = clock.instant();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.username())                       // the services use "sub" as the customer ID
                .claim("roles", user.roles())
                .issuedAt(now)
                .expiresAt(now.plus(LIFETIME))
                .build();
        String token = encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();
        return new TokenResponse(token, "Bearer", LIFETIME.toSeconds());
    }

    private static boolean samePassword(String expected, String given) {
        // constant time: the answer time does not tell how many characters were right
        return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), given.getBytes(StandardCharsets.UTF_8));
    }
}
