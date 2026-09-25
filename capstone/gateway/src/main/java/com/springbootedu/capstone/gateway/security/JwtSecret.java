package com.springbootedu.capstone.gateway.security;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * ADR-5 — the HMAC key all services share (from the environment). The gateway checks tokens with it and,
 * in the dev profile, signs them.
 */
@Component
public class JwtSecret {

    private final SecretKey key;

    JwtSecret(@Value("${bookstore.jwt.secret}") String secret) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("bookstore.jwt.secret must have at least 32 bytes (HS256)");
        }
        this.key = new SecretKeySpec(bytes, "HmacSHA256");
    }

    public SecretKey key() {
        return key;
    }
}
