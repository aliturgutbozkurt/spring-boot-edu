package com.springbootedu.springcloud.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Exercise 3 — the valid API key, from the configuration.
 */
// TODO 3: a refreshed configuration (POST /actuator/refresh) must reach this bean without a restart
@Component
public class ApiKeys {

    private final String validKey;

    ApiKeys(@Value("${bookstore.gateway.api-key}") String validKey) {
        this.validKey = validKey;
    }

    public boolean isValid(String key) {
        return validKey.equals(key);
    }
}
