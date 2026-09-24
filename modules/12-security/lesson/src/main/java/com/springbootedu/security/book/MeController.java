package com.springbootedu.security.book;

import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lesson 3.5 — shows who the caller is, however they authenticated (Basic or JWT).
 */
@RestController
class MeController {

    @GetMapping("/api/me")
    Map<String, Object> me(Authentication authentication) {
        List<String> authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return Map.of("name", authentication.getName(), "authorities", authorities,
                "type", authentication.getClass().getSimpleName());
    }
}
