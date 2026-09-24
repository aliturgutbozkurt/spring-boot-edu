package com.springbootedu.security.given;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Given: three users in memory (the lesson keeps them in PostgreSQL).
 */
@Configuration(proxyBeanMethods = false)
class DemoUsers {

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    UserDetailsService users(PasswordEncoder passwords) {
        return new InMemoryUserDetailsManager(
                User.withUsername("ada").password(passwords.encode("ada-password")).roles("CUSTOMER").build(),
                User.withUsername("bob").password(passwords.encode("bob-password")).roles("CUSTOMER").build(),
                User.withUsername("admin").password(passwords.encode("admin-password")).roles("ADMIN").build());
    }
}
