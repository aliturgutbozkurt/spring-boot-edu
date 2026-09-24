package com.springbootedu.reactive.review;

import com.springbootedu.reactive.book.Book;
import com.springbootedu.reactive.book.BookRepository;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Lesson 3.5 — an annotated controller, like in Spring MVC, but returning Mono and Flux.
 */
@RestController
@RequestMapping("/api/books/{isbn}")
class ReviewController {

    private final BookRepository books;
    private final ReviewRepository reviews;

    ReviewController(BookRepository books, ReviewRepository reviews) {
        this.books = books;
        this.reviews = reviews;
    }

    @GetMapping("/reviews")
    Flux<Review> reviews(@PathVariable String isbn) {
        return reviews.findByIsbnOrderByStarsDesc(isbn);
    }

    @PostMapping("/reviews")
    Mono<ResponseEntity<Review>> add(@PathVariable String isbn, @RequestBody NewReview review) {
        return reviews.save(new Review(null, isbn, review.author(), review.stars(), review.text()))
                .map(saved -> ResponseEntity.created(URI.create("/api/books/" + isbn + "/reviews")).body(saved));
    }

    // tag::zip[]
    @GetMapping("/details")
    Mono<ResponseEntity<BookDetails>> details(@PathVariable String isbn) {
        return Mono.zip(books.findByIsbn(isbn),                               // PostgreSQL …
                        reviews.findByIsbnOrderByStarsDesc(isbn).collectList()) // … and MongoDB, at the same time
                .map(both -> ResponseEntity.ok(toDetails(both.getT1(), both.getT2())))
                .defaultIfEmpty(ResponseEntity.notFound().build());          // no book → zip is empty → 404
    }
    // end::zip[]

    private static BookDetails toDetails(Book book, List<Review> reviews) {
        double average = reviews.stream().mapToInt(Review::stars).average().orElse(0);
        return new BookDetails(book.isbn(), book.title(), book.price(), average, reviews);
    }
}
