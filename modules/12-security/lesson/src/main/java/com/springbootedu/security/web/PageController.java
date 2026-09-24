package com.springbootedu.security.web;

import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lessons 3.1 and 3.3 — pages of the browser chain (plain text to keep the focus on security).
 */
@RestController
class PageController {

    @GetMapping("/")
    String home() {
        return "Bookstore — public page. Log in at /login";
    }

    @GetMapping("/account")
    String account(Principal principal) {
        return "Hello, " + principal.getName();
    }

    @PostMapping("/account/newsletter")
    String subscribe(Principal principal) {
        return principal.getName() + " subscribed to the newsletter";   // a state change: needs a CSRF token
    }
}
