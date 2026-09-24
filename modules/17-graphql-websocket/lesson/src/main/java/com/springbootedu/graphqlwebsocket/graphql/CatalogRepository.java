package com.springbootedu.graphqlwebsocket.graphql;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/**
 * Lessons 3.2–3.3 — plain SQL with JdbcClient. The counters make the N+1 problem visible in tests and in the tour.
 */
@Repository
public class CatalogRepository {

    private final JdbcClient jdbc;
    private final AtomicInteger authorQueries = new AtomicInteger();
    private final AtomicInteger reviewQueries = new AtomicInteger();

    CatalogRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<Book> findAllBooks() {
        return jdbc.sql("SELECT isbn, title, price, author_id FROM book ORDER BY title")
                .query(Book.class)
                .list();
    }

    public Optional<Book> findBook(String isbn) {
        return jdbc.sql("SELECT isbn, title, price, author_id FROM book WHERE isbn = ?")
                .param(isbn)
                .query(Book.class)
                .optional();
    }

    // tag::authors-by-ids[]
    /** One query for many authors: this is what @BatchMapping calls once per request. */
    public List<Author> findAuthors(Collection<Long> ids) {
        authorQueries.incrementAndGet();
        return jdbc.sql("SELECT id, name FROM author WHERE id IN (:ids)")
                .param("ids", ids)
                .query(Author.class)
                .list();
    }
    // end::authors-by-ids[]

    /** One query per book: called by @SchemaMapping for every book in the result (N+1). */
    public List<Review> findReviews(String isbn) {
        reviewQueries.incrementAndGet();
        return jdbc.sql("SELECT id, isbn, stars, text FROM review WHERE isbn = ? ORDER BY id")
                .param(isbn)
                .query(Review.class)
                .list();
    }

    public Review insertReview(ReviewInput input) {
        long id = jdbc.sql("INSERT INTO review (isbn, stars, text) VALUES (?, ?, ?) RETURNING id")
                .params(input.isbn(), input.stars(), input.text())
                .query(Long.class)
                .single();
        return new Review(id, input.isbn(), input.stars(), input.text());
    }

    public void resetCounters() {
        authorQueries.set(0);
        reviewQueries.set(0);
    }

    public int authorQueries() {
        return authorQueries.get();
    }

    public int reviewQueries() {
        return reviewQueries.get();
    }
}
