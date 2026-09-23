package com.springbootedu.datajpapostgres;

import com.springbootedu.datajpapostgres.catalog.Author;
import com.springbootedu.datajpapostgres.catalog.AuthorRepository;
import com.springbootedu.datajpapostgres.catalog.Book;
import com.springbootedu.datajpapostgres.catalog.BookCard;
import com.springbootedu.datajpapostgres.catalog.BookFilter;
import com.springbootedu.datajpapostgres.catalog.BookRepository;
import com.springbootedu.datajpapostgres.catalog.BookSearch;
import com.springbootedu.datajpapostgres.catalog.BookTitleAndPrice;
import com.springbootedu.datajpapostgres.catalog.Category;
import com.springbootedu.datajpapostgres.stock.StockService;
import jakarta.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Runs every example of the lesson once, in the order of the lesson notes (section 3).
 * Start it with: ./mvnw -pl modules/06-data-jpa-postgres/lesson -am spring-boot:run   (Docker must be running)
 */
@Component
@ConditionalOnBooleanProperty(name = "bookstore.tour.enabled", matchIfMissing = true)
class LessonTour implements ApplicationRunner {

    private final AuthorRepository authors;
    private final BookRepository books;
    private final BookSearch search;
    private final StockService stock;
    private final TransactionTemplate tx;
    private final Statistics statistics;

    LessonTour(AuthorRepository authors, BookRepository books, BookSearch search, StockService stock,
               TransactionTemplate tx, EntityManagerFactory entityManagerFactory) {
        this.authors = authors;
        this.books = books;
        this.search = search;
        this.stock = stock;
        this.tx = tx;
        this.statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
    }

    @Override
    public void run(ApplicationArguments args) {
        section("3.1 Entities and relationships");
        inTransaction(() -> {
            Book book = books.findByIsbn("9780134685991").orElseThrow();
            print(book.getTitle() + " by " + book.getAuthor().getName() + ", categories "
                    + book.getCategories().stream().map(Category::getName).sorted().toList());
            return null;
        });

        section("3.2 Derived query with paging");
        var page = books.findByTitleContainingIgnoreCase("java", PageRequest.of(0, 2, Sort.by("price").descending()));
        print(page.getContent().stream().map(Book::getTitle).toList() + " — page 1 of " + page.getTotalPages()
                + ", " + page.getTotalElements() + " matches");

        section("3.3 N+1");
        print("findAll + touching books:  " + statements(() -> countBooks(authors.findAll())) + " SQL statements");
        print("@EntityGraph:              " + statements(() -> countBooks(authors.findAllWithBooksBy())) + " SQL statement");
        print("join fetch:                " + statements(() -> countBooks(authors.findAllFetchingBooks())) + " SQL statement");

        section("3.4 Projections");
        books.findByAuthorNameOrderByTitle("Joshua Bloch")
                .forEach((BookTitleAndPrice b) -> print("interface: " + b.getTitle() + " " + b.getPrice()));
        books.findCardsCheaperThan(new BigDecimal("86")).forEach(card -> print("record:    " + card));

        section("3.5 Specifications");
        search.search(new BookFilter("java", new BigDecimal("100"), "java"), PageRequest.of(0, 10, Sort.by("title")))
                .forEach((BookCard card) -> print(card.title() + " · " + card.author() + " · " + card.price()));

        section("3.6 Auditing and @Version");
        Book refactoring = books.findByIsbn("9780134757599").orElseThrow();
        refactoring.changePrice(refactoring.getPrice().add(BigDecimal.ONE));
        Book saved = books.save(refactoring);
        print("price " + saved.getPrice() + ", version " + saved.getVersion() + ", updated " + saved.getUpdatedAt());

        section("3.7 Pessimistic locking: 10 buyers, 1 copy each, 3 in stock");
        AtomicInteger sold = new AtomicInteger();
        int before = books.findByIsbn("9781492076988").orElseThrow().getStock();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 10; i++) {
                executor.submit(() -> {
                    if (stock.trySell("9781492076988", 1)) {
                        sold.incrementAndGet();
                    }
                });
            }
        }
        print("stock before " + before + ", sold " + sold.get() + ", stock after "
                + books.findByIsbn("9781492076988").orElseThrow().getStock());
    }

    private <T> T inTransaction(Supplier<T> work) {        // lazy relationships need an open transaction
        return tx.execute(status -> work.get());
    }

    private long statements(Supplier<Integer> work) {
        statistics.clear();
        inTransaction(work);
        return statistics.getPrepareStatementCount();
    }

    private static int countBooks(List<Author> list) {
        return list.stream().mapToInt(author -> author.getBooks().size()).sum();
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("== " + title);
    }

    private static void print(String line) {
        System.out.println(line);
    }
}
