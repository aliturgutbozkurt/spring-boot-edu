package com.springbootedu.webmvc.book;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Lessons 3.1–3.6 — the Bookstore REST API. Thin: HTTP in, DTO out; the logic lives in {@link BookService}.
 */
@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Kitap kataloğu / Book catalogue")
public class BookController {

    private final BookService books;

    public BookController(BookService books) {
        this.books = books;
    }

    // tag::paging[]
    @GetMapping
    @Operation(summary = "Kitapları sayfa sayfa listele / List books page by page")
    public PageResponse<BookResponse> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(50) int size,         // protect the server
            @RequestParam(defaultValue = "title") @Pattern(regexp = "title|price|publishedOn") String sort,
            @RequestParam(defaultValue = "asc") @Pattern(regexp = "asc|desc") String direction) {
        PageResponse<Book> result = books.findPage(page, size, BookService.SortField.from(sort), direction.equals("desc"));
        return new PageResponse<>(result.content().stream().map(BookResponse::from).toList(),
                result.page(), result.size(), result.totalElements(), result.totalPages());
    }
    // end::paging[]

    // tag::csv[]
    @GetMapping(produces = "text/csv")                     // same URL, chosen by the Accept header
    public String exportCsv() {
        return books.findAll().stream()
                .map(book -> book.id() + "," + book.isbn().value() + "," + book.title() + "," + book.price())
                .collect(Collectors.joining("\n", "id,isbn,title,price\n", "\n"));
    }
    // end::csv[]

    // tag::get[]
    @GetMapping(path = "/{id}", version = "1")
    @Operation(summary = "Bir kitabı getir (v1) / Get a book (v1)")
    public BookResponse get(@PathVariable @Positive long id) {
        return BookResponse.from(books.find(id));          // BookNotFoundException → 404 ProblemDetail
    }
    // end::get[]

    // tag::get-v2[]
    @GetMapping(path = "/{id}", version = "2")             // selected with the header "API-Version: 2"
    @Operation(summary = "Bir kitabı getir (v2) / Get a book (v2)")
    public BookResponseV2 getV2(@PathVariable @Positive long id) {
        return BookResponseV2.from(books.find(id));
    }
    // end::get-v2[]

    // tag::create[]
    @PostMapping
    @Operation(summary = "Kitap ekle / Add a book")
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest request) {
        Book created = books.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(BookResponse.from(created));   // 201 + Location header
    }
    // end::create[]

    @PutMapping("/{id}")
    @Operation(summary = "Kitabı güncelle / Update a book")
    public BookResponse update(@PathVariable @Positive long id, @Valid @RequestBody BookRequest request) {
        return BookResponse.from(books.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Kitabı sil / Delete a book")
    public void delete(@PathVariable @Positive long id) {
        books.delete(id);
    }
}
