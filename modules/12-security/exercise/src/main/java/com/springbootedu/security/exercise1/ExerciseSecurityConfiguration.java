package com.springbootedu.security.exercise1;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Exercise 1 — one stateless chain for the API: HTTP Basic or a JWT.
 */
@Configuration(proxyBeanMethods = false)
public class ExerciseSecurityConfiguration {

    @Bean
    SecurityFilterChain apiChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                        // TODO 1a: GET /api/catalog is public; POST /api/catalog only for ADMIN
                        // TODO 1b: /api/cart/** only for CUSTOMER
                        // TODO 1c: /api/admin/** only for ADMIN
                        .anyRequest().authenticated())
                .httpBasic(withDefaults())
                .oauth2ResourceServer(resourceServer -> resourceServer.jwt(withDefaults()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable());
        return http.build();
    }
}
