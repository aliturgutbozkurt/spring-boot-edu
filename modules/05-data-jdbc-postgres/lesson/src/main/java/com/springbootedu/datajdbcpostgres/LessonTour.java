package com.springbootedu.datajdbcpostgres;

import com.springbootedu.datajdbcpostgres.book.JdbcBookRepository;
import com.springbootedu.datajdbcpostgres.checkout.CheckoutService;
import com.springbootedu.datajdbcpostgres.checkout.OutOfStockException;
import com.springbootedu.datajdbcpostgres.order.OrderLine;
import com.springbootedu.datajdbcpostgres.order.PurchaseOrder;
import com.springbootedu.datajdbcpostgres.order.PurchaseOrderRepository;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

/**
 * Runs every example of the lesson once, in the order of the lesson notes (section 3).
 * Start it with: ./mvnw -pl modules/05-data-jdbc-postgres/lesson -am spring-boot:run   (Docker must be running)
 */
@Component
@ConditionalOnBooleanProperty(name = "bookstore.tour.enabled", matchIfMissing = true)
class LessonTour implements ApplicationRunner {

    private final DataSource dataSource;
    private final JdbcClient jdbc;
    private final JdbcBookRepository books;
    private final PurchaseOrderRepository orders;
    private final CheckoutService checkout;

    LessonTour(DataSource dataSource, JdbcClient jdbc, JdbcBookRepository books, PurchaseOrderRepository orders,
               CheckoutService checkout) {
        this.dataSource = dataSource;
        this.jdbc = jdbc;
        this.books = books;
        this.orders = orders;
        this.checkout = checkout;
    }

    @Override
    public void run(ApplicationArguments args) throws SQLException {
        section("3.1 DataSource (from Docker Compose)");
        try (Connection connection = dataSource.getConnection()) {
            var meta = connection.getMetaData();
            print(meta.getURL() + " · " + meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion()
                    + " · pool " + dataSource.getClass().getSimpleName());
        }

        section("3.2 Flyway");
        jdbc.sql("select version, description from flyway_schema_history order by installed_rank")
                .query((rs, row) -> "V" + rs.getString(1) + " " + rs.getString(2)).list()
                .forEach(LessonTour::print);

        section("3.3 JdbcClient");
        books.findAll().forEach(book -> print(book.toString()));
        print(books.findWithAuthors().getFirst().toString());

        section("3.4 Spring Data JDBC aggregate");
        Long id = orders.save(PurchaseOrder.create("demo@example.com",
                Set.of(new OrderLine("9781617293566", 2, new BigDecimal("120.00"))))).id();
        PurchaseOrder loaded = orders.findById(id).orElseThrow();
        print("order #" + id + " with " + loaded.lines().size() + " line(s), total " + loaded.total());

        section("3.5 + 3.6 Transactions and transactional events");
        try {
            long orderId = checkout.placeOrder("ayse@example.com", Map.of("9781617297571", 1));
            print("checkout OK → order #" + orderId + ", stock of Spring in Action now "
                    + books.findByIsbn("9781617297571").orElseThrow().stock());
        } catch (OutOfStockException exception) {
            // The data lives in a Docker volume and survives restarts; every run sells one more copy.
            print("sold out after several runs — reset the data: docker compose --profile postgres down -v");
        }
        try {
            checkout.placeOrder("ayse@example.com", Map.of("9780134757599", 99));
        } catch (OutOfStockException exception) {
            print("checkout FAILED → " + exception.getMessage());
        }
        print("audit_log rows: " + count("audit_log") + " (both attempts, thanks to REQUIRES_NEW)");
        print("notification rows: " + count("notification") + " (only after a commit)");
    }

    private int count(String table) {
        return jdbc.sql("select count(*) from " + table).query(Integer.class).single();
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("== " + title);
    }

    private static void print(String line) {
        System.out.println(line);
    }
}
