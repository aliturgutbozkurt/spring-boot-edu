package com.springbootedu.security.config;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Lessons 3.1–3.4 and 3.8 — one filter chain for the API, one for the browser, plus users and passwords.
 */
@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity                                   // @PreAuthorize / @PostAuthorize (lesson 3.4)
public class SecurityConfiguration {

    // tag::api-chain[]
    @Bean
    @Order(2)
    SecurityFilterChain apiChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**")
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/books/**")
                            .hasAnyAuthority("ROLE_ADMIN", "SCOPE_books.write")   // a person or a token
                        .requestMatchers("/api/me", "/api/orders/**").authenticated()
                        .anyRequest().denyAll())                          // deny what no rule allows
                .httpBasic(withDefaults())                                        // username + password (curl, scripts) …
                .oauth2ResourceServer(resourceServer -> resourceServer.jwt(withDefaults()))   // … or a JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // No session cookie. Careful: browsers cache Basic credentials and send them on their own —
                // for a browser front end use bearer tokens only, or keep CSRF on (lesson 3.3)
                .csrf(csrf -> csrf.disable())
                .cors(withDefaults());                         // uses the CorsConfigurationSource bean
        return http.build();
    }
    // end::api-chain[]

    // tag::web-chain[]
    @Bean
    @Order(3)
    SecurityFilterChain webChain(HttpSecurity http, ObjectProvider<ClientRegistrationRepository> oauth2Clients)
            throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/error").permitAll()
                        .anyRequest().authenticated())
                .formLogin(withDefaults())                     // Spring Security generates /login
                .logout(withDefaults());                       // CSRF protection stays on (the default)
        if (oauth2Clients.getIfAvailable() != null) {          // only if a provider is configured (lesson 3.7)
            http.oauth2Login(withDefaults());
        }
        return http.build();
    }
    // end::web-chain[]

    // tag::users[]
    @Bean
    UserDetailsManager users(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);        // tables "users" and "authorities" (Flyway V1)
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();   // bcrypt now, other ids still readable
    }
    // end::users[]

    // tag::cors[]
    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${bookstore.cors.allowed-origins}") List<String> origins) {
        var api = new CorsConfiguration();
        api.setAllowedOrigins(origins);                        // never "*" together with credentials
        api.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        api.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        api.setMaxAge(3600L);                                  // browsers may cache the preflight for an hour
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", api);
        return source;
    }
    // end::cors[]
}
