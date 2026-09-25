package com.springbootedu.springcloud.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * Exercise 3 — the valid API key, from the configuration; a refresh creates this bean again.
 */
@Component
@RefreshScope
public class ApiKeys {

    private final String validKey;

    ApiKeys(@Value("${bookstore.gateway.api-key}") String validKey) {
        this.validKey = validKey;
    }

    public boolean isValid(String key) {
        return validKey.equals(key);
    }
}
