package com.springbootedu.datajdbcpostgres.exercise2;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 * Exercise 2 — reviews with JdbcClient; batches with NamedParameterJdbcTemplate.
 */
@Repository
public class ReviewRepository {

    private static final String INSERT = """
            insert into review (book_id, stars, comment)
            values ((select id from book where isbn = :isbn), :stars, :comment)""";

    private final JdbcClient jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;

    public ReviewRepository(JdbcClient jdbc, NamedParameterJdbcTemplate namedJdbc) {
        this.jdbc = jdbc;
        this.namedJdbc = namedJdbc;
    }

    public long add(NewReview review) {
        KeyHolder keys = new GeneratedKeyHolder();
        jdbc.sql(INSERT).paramSource(review).update(keys, "id");
        return keys.getKeyAs(Long.class);
    }

    public List<Review> findByIsbn(String isbn) {
        return jdbc.sql("""
                        select r.id, r.stars, r.comment, r.created_at
                        from review r join book b on b.id = r.book_id
                        where b.isbn = :isbn
                        order by r.created_at desc, r.id desc""")
                .param("isbn", isbn)
                .query(Review.class)
                .list();
    }

    public Optional<Double> averageStars(String isbn) {
        return jdbc.sql("""
                        select avg(r.stars)::float8 from review r join book b on b.id = r.book_id where b.isbn = :isbn""")
                .param("isbn", isbn)
                .query((row, number) -> Optional.ofNullable(row.getObject(1, Double.class)))   // avg() of no rows is NULL
                .single();
    }

    public int[] addAll(List<NewReview> reviews) {
        return namedJdbc.batchUpdate(INSERT, SqlParameterSourceUtils.createBatch(reviews));
    }
}
