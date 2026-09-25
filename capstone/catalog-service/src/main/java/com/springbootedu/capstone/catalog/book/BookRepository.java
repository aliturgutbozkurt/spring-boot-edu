package com.springbootedu.capstone.catalog.book;

import org.springframework.data.repository.ListCrudRepository;

public interface BookRepository extends ListCrudRepository<Book, String> {
}
