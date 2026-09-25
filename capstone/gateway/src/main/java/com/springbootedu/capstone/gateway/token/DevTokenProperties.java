package com.springbootedu.capstone.gateway.token;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * The demo users of the dev token endpoint (application-dev.yaml).
 */
@ConfigurationProperties("bookstore.dev-tokens")
public record DevTokenProperties(boolean enabled, @DefaultValue List<DemoUser> users) {

    public record DemoUser(String username, String password, List<String> roles) {
    }
}
