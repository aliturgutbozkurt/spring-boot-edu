package com.springbootedu.capstone.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * ADR-5 — the gateway rejects requests without a valid token before they reach a service. The services
 * check the same token again (defence in depth); the Authorization header is forwarded unchanged.
 */
@Configuration(proxyBeanMethods = false)
class SecurityConfiguration {

    @Bean
    SecurityWebFilterChain gatewaySecurity(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.POST, "/api/auth/token").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/catalog/**", "/api/search/**").permitAll()
                        .pathMatchers("/api/catalog/**").hasRole("ADMIN")
                        .pathMatchers("/api/orders/**").authenticated()
                        .pathMatchers("/actuator/health/**").permitAll()
                        .anyExchange().denyAll())
                .oauth2ResourceServer(server -> server.jwt(jwt -> jwt.jwtAuthenticationConverter(
                        new ReactiveJwtAuthenticationConverterAdapter(rolesFromClaim()))))
                .csrf(csrf -> csrf.disable())                   // a stateless API with bearer tokens: no cookies
                .build();
    }

    @Bean
    ReactiveJwtDecoder jwtDecoder(JwtSecret secret) {
        return NimbusReactiveJwtDecoder.withSecretKey(secret.key()).macAlgorithm(MacAlgorithm.HS256).build();
    }

    private static JwtAuthenticationConverter rolesFromClaim() {
        var authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("roles");          // "roles": ["USER", "ADMIN"] → ROLE_USER, ROLE_ADMIN
        authorities.setAuthorityPrefix("ROLE_");
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }
}
