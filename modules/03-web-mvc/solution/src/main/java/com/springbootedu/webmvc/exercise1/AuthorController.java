package com.springbootedu.webmvc.exercise1;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Exercise 1 — the author endpoint.
 */
@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorRepository authors;

    public AuthorController(AuthorRepository authors) {
        this.authors = authors;
    }

    @GetMapping
    public List<Author> list() {
        return authors.findAll();
    }

    @GetMapping("/{id}")
    public Author get(@PathVariable long id) {
        return authors.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No author with id " + id));
    }

    @PostMapping
    public ResponseEntity<Author> create(@Valid @RequestBody AuthorRequest request) {
        Author created = authors.save(Objects.requireNonNull(request.name()), Objects.requireNonNull(request.country()));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }
}
