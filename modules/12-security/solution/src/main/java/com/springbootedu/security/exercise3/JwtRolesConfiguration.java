package com.springbootedu.security.exercise3;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

/**
 * Exercise 3 — turns the claims of our identity provider into a Spring Security authentication.
 */
@Configuration(proxyBeanMethods = false)
public class JwtRolesConfiguration {

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setPrincipalClaimName("preferred_username");
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> roles = Objects.requireNonNullElse(jwt.getClaimAsStringList("roles"), List.of());
            Collection<GrantedAuthority> authorities = roles.stream()
                    .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role.toUpperCase(Locale.ROOT)))
                    .toList();
            return authorities;
        });
        return converter;
    }
}
