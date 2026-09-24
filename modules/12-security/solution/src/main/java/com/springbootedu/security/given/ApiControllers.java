package com.springbootedu.security.given;

import java.net.URI;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Given: the endpoints the exercises protect. They contain no security code themselves.
 */
@RestController
class ApiControllers {

    record CatalogEntry(String isbn, String title) {
    }

    @GetMapping("/api/catalog")
    List<CatalogEntry> catalog() {
        return List.of(new CatalogEntry("9780134685991", "Effective Java"));
    }

    @PostMapping("/api/catalog")
    ResponseEntity<CatalogEntry> add(@RequestBody CatalogEntry entry) {
        return ResponseEntity.created(URI.create("/api/catalog/" + entry.isbn())).body(entry);
    }

    @GetMapping("/api/cart")
    List<String> cart() {
        return List.of("9780134685991");
    }

    @GetMapping("/api/admin/report")
    Map<String, Integer> report() {
        return Map.of("ordersToday", 42);
    }

    @GetMapping("/api/me")
    Map<String, Object> me(Authentication authentication) {
        return Map.of("name", authentication.getName(), "authorities",
                authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    }
}
