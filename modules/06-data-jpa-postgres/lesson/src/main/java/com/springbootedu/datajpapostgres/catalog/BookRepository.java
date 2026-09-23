package com.springbootedu.datajpapostgres.catalog;

import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 * Lessons 3.2–3.7 — derived queries, JPQL, projections, specifications and locks.
 */
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    // tag::derived[]
    Optional<Book> findByIsbn(String isbn);

    Page<Book> findByTitleContainingIgnoreCase(String text, Pageable pageable);   // paging + sorting for free
    // end::derived[]

    // tag::projections[]
    List<BookTitleAndPrice> findByAuthorNameOrderByTitle(String authorName);      // interface projection

    @Query("""
            select new com.springbootedu.datajpapostgres.catalog.BookCard(b.title, a.name, b.price)
            from Book b join b.author a
            where b.price < :max
            order by b.price""")
    List<BookCard> findCardsCheaperThan(BigDecimal max);                          // record (DTO) projection
    // end::projections[]

    @Override
    @EntityGraph(attributePaths = "author")                 // search results show the author: fetch it too
    Page<Book> findAll(Specification<Book> specification, Pageable pageable);

    // tag::lock[]
    @Lock(LockModeType.PESSIMISTIC_WRITE)                  // SELECT … FOR UPDATE
    @Query("select b from Book b where b.isbn = :isbn")
    Optional<Book> findForUpdate(String isbn);
    // end::lock[]
}
