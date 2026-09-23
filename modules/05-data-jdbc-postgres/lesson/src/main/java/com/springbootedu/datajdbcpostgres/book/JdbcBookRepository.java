package com.springbootedu.datajdbcpostgres.book;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 * Lesson 3.3 — plain SQL with JdbcClient: you write the SQL, Spring handles connections, parameters and mapping.
 */
@Repository
public class JdbcBookRepository {

    private final JdbcClient jdbc;

    public JdbcBookRepository(JdbcClient jdbc) {          // auto-configured by Spring Boot
        this.jdbc = jdbc;
    }

    // tag::query[]
    public List<Book> findAll() {
        return jdbc.sql("select id, isbn, title, price, stock from book order by id")
                .query(Book.class)                          // columns → record components
                .list();
    }

    public Optional<Book> findByIsbn(String isbn) {
        return jdbc.sql("select id, isbn, title, price, stock from book where isbn = :isbn")
                .param("isbn", isbn)                        // named parameter: no SQL injection
                .query(Book.class)
                .optional();
    }
    // end::query[]

    // tag::join[]
    public List<BookWithAuthor> findWithAuthors() {
        return jdbc.sql("""
                        select b.title, a.name as author_name
                        from book b join author a on a.id = b.author_id
                        order by b.title""")
                .query(BookWithAuthor.class)
                .list();
    }
    // end::join[]

    // tag::insert[]
    public long insert(Book book) {
        KeyHolder keys = new GeneratedKeyHolder();
        jdbc.sql("insert into book (isbn, title, price, stock) values (:isbn, :title, :price, :stock)")
                .paramSource(book)                          // parameters from the record's components
                .update(keys, "id");                        // ask PostgreSQL for the generated id
        return keys.getKeyAs(Long.class);
    }
    // end::insert[]

    public int changeStock(String isbn, int delta) {
        return jdbc.sql("update book set stock = stock + :delta where isbn = :isbn")
                .param("delta", delta)
                .param("isbn", isbn)
                .update();                                  // number of changed rows
    }
}
