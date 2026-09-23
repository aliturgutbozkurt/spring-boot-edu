package com.springbootedu.datajpapostgres.catalog;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Lesson 3.3 — three ways to load authors with their books.
 */
// tag::n-plus-one[]
public interface AuthorRepository extends JpaRepository<Author, Long> {

    Optional<Author> findByName(String name);

    // findAll() (inherited): 1 query for authors, then 1 query per author when books are touched → N+1

    @EntityGraph(attributePaths = "books")                  // fix 1: fetch the books in the same query
    List<Author> findAllWithBooksBy();

    @Query("select distinct a from Author a left join fetch a.books")   // fix 2: JPQL join fetch
    List<Author> findAllFetchingBooks();
}
// end::n-plus-one[]
