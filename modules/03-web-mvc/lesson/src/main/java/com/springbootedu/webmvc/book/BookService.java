package com.springbootedu.webmvc.book;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

/**
 * Business logic; knows nothing about HTTP.
 */
@Service
public class BookService {

    public enum SortField {
        TITLE, PRICE, PUBLISHED_ON;

        /** "title", "price", "publishedOn" — the names used in the query string. */
        public static SortField from(String name) {
            return switch (name) {
                case "price" -> PRICE;
                case "publishedOn" -> PUBLISHED_ON;
                default -> TITLE;
            };
        }
    }

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public PageResponse<Book> findPage(int page, int size, SortField sort, boolean descending) {
        Comparator<Book> order = switch (sort) {
            case TITLE -> Comparator.comparing(Book::title);
            case PRICE -> Comparator.comparing(Book::price);
            case PUBLISHED_ON -> Comparator.comparing(Book::publishedOn);
        };
        List<Book> sorted = repository.findAll().stream().sorted(descending ? order.reversed() : order).toList();
        List<Book> content = sorted.stream().skip((long) page * size).limit(size).toList();
        int totalPages = (int) Math.ceil((double) sorted.size() / size);
        return new PageResponse<>(content, page, size, sorted.size(), totalPages);
    }

    public List<Book> findAll() {
        return repository.findAll();
    }

    public Book find(long id) {
        return repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    public Book create(BookRequest request) {
        return repository.save(toBook(0, request));
    }

    public Book update(long id, BookRequest request) {
        find(id);                                          // 404 if it does not exist
        return repository.save(toBook(id, request));
    }

    public void delete(long id) {
        if (!repository.deleteById(id)) {
            throw new BookNotFoundException(id);
        }
    }

    private static Book toBook(long id, BookRequest request) {
        // the request was validated before it reached the service, so these values are present
        return new Book(id, new Isbn(Objects.requireNonNull(request.isbn())), Objects.requireNonNull(request.title()),
                List.copyOf(Objects.requireNonNull(request.authors())), Objects.requireNonNull(request.price()),
                Objects.requireNonNull(request.publishedOn()));
    }
}
