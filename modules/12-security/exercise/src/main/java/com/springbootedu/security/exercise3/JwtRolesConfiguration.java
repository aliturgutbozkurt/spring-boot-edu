package com.springbootedu.security.exercise3;

import org.springframework.context.annotation.Configuration;

/**
 * Exercise 3 — turns the claims of our identity provider into a Spring Security authentication.
 */
@Configuration(proxyBeanMethods = false)
public class JwtRolesConfiguration {

    // TODO 3a: a JwtAuthenticationConverter bean — the resource server picks it up automatically
    // TODO 3b: the principal name comes from the "preferred_username" claim
    // TODO 3c: every entry of the "roles" claim becomes an authority "ROLE_<ROLE IN CAPITALS>"
}
