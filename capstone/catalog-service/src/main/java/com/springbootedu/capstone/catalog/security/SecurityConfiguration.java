package com.springbootedu.capstone.catalog.security;

import java.nio.charset.StandardCharsets;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.grpc.server.security.AuthenticationProcessInterceptor;
import org.springframework.grpc.server.security.GrpcSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * ADR-5 — the catalog is a resource server too: public reads, writes only for ADMIN.
 * The gRPC port is internal (not routed by the gateway), but it checks the same JWT: the order service
 * forwards the customer's token.
 */
@Configuration(proxyBeanMethods = false)
class SecurityConfiguration {

    @Bean
    SecurityFilterChain api(HttpSecurity http) {
        return http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(HttpMethod.GET, "/api/catalog/**").permitAll()
                        .requestMatchers("/actuator/health/**").permitAll()
                        .requestMatchers("/api/**").hasRole("ADMIN")
                        .anyRequest().denyAll())
                .oauth2ResourceServer(server -> server.jwt(jwt -> jwt.jwtAuthenticationConverter(rolesFromClaim())))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())                   // a stateless API with bearer tokens: no cookies
                .build();
    }

    @Bean
    @GlobalServerInterceptor
    AuthenticationProcessInterceptor grpcAuthentication(GrpcSecurity grpc, JwtDecoder decoder) throws Exception {
        return grpc
                .authorizeRequests(requests -> requests
                        .methods("grpc.*/*").permitAll()                    // health and reflection
                        .allRequests().authenticated())
                .oauth2ResourceServer(server -> server.jwt(jwt -> jwt.decoder(decoder)
                        .jwtAuthenticationConverter(rolesFromClaim())))
                .build();
    }

    @Bean
    JwtDecoder jwtDecoder(@Value("${bookstore.jwt.secret}") String secret) {
        byte[] key = secret.getBytes(StandardCharsets.UTF_8);
        if (key.length < 32) {
            throw new IllegalStateException("bookstore.jwt.secret must have at least 32 bytes (HS256)");
        }
        return NimbusJwtDecoder.withSecretKey(new SecretKeySpec(key, "HmacSHA256")).macAlgorithm(MacAlgorithm.HS256).build();
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
