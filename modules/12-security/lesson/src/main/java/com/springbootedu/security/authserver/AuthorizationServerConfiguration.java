package com.springbootedu.security.authserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Lesson 3.6 — the first chain handles only the Authorization Server endpoints (/oauth2/token, /oauth2/jwks …).
 * The registered clients come from application.yaml; Boot creates the signing key and the JwtDecoder.
 */
// tag::authorization-server-chain[]
@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfiguration {

    @Bean
    @Order(1)
    SecurityFilterChain authorizationServerChain(HttpSecurity http) throws Exception {
        var authorizationServer = new OAuth2AuthorizationServerConfigurer();
        http.securityMatcher(authorizationServer.getEndpointsMatcher())          // only these URLs
                .with(authorizationServer, Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated());
        return http.build();
    }
}
// end::authorization-server-chain[]
