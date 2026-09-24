package com.springbootedu.modulith;

import com.springbootedu.modulith.inventory.Inventory;
import com.springbootedu.modulith.inventory.Warehouse;
import com.springbootedu.modulith.notification.Mailbox;
import com.springbootedu.modulith.order.Order;
import com.springbootedu.modulith.order.OrderService;
import java.util.function.BooleanSupplier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.modulith.events.IncompleteEventPublications;
import org.springframework.stereotype.Component;

/**
 * Runs the lesson's examples once (section 3).
 * Start it with: ./mvnw -pl modules/18-modulith/lesson spring-boot:run   (Docker must be running)
 */
@Component
@ConditionalOnBooleanProperty(name = "bookstore.tour.enabled", matchIfMissing = true)
class LessonTour implements ApplicationRunner {

    private static final String EFFECTIVE_JAVA = "9780134685991";

    private final OrderService orders;
    private final Inventory inventory;
    private final Mailbox mailbox;
    private final Warehouse warehouse;
    private final IncompleteEventPublications incompletePublications;
    private final JdbcClient jdbc;

    LessonTour(OrderService orders, Inventory inventory, Mailbox mailbox, Warehouse warehouse,
               IncompleteEventPublications incompletePublications, JdbcClient jdbc) {
        this.orders = orders;
        this.inventory = inventory;
        this.mailbox = mailbox;
        this.warehouse = warehouse;
        this.incompletePublications = incompletePublications;
        this.jdbc = jdbc;
    }

    @Override
    public void run(ApplicationArguments args) throws InterruptedException {
        section("3.2–3.3 An order, and two modules that react to it");
        int before = inventory.available(EFFECTIVE_JAVA);
        Order order = orders.place("tour", EFFECTIVE_JAVA, 2);
        waitUntil(() -> inventory.available(EFFECTIVE_JAVA) == before - 2 && !mailbox.sentTo("tour").isEmpty());
        print("placed " + order);
        print("inventory: " + before + " → " + inventory.available(EFFECTIVE_JAVA));
        print("mail: " + mailbox.sentTo("tour").getLast());
        printPublications(order.id());

        section("3.4 The warehouse is offline: the inventory listener fails");
        warehouse.goOffline();
        Order second = orders.place("tour", EFFECTIVE_JAVA, 1);
        waitUntil(() -> "FAILED".equals(inventoryStatus(second.id())));
        printPublications(second.id());

        section("3.4 Back online: resubmit the incomplete publications");
        warehouse.goOnline();
        incompletePublications.resubmitIncompletePublications(publication -> true);
        waitUntil(() -> "COMPLETED".equals(inventoryStatus(second.id())));
        printPublications(second.id());
        print("inventory now: " + inventory.available(EFFECTIVE_JAVA));
        print("the event is also in the Kafka topic bookstore.orders (key = customer id)");

        section("3.6 Documentation");
        print("run ModularityTest, then open lesson/target/spring-modulith-docs (PlantUML + module canvases)");
    }

    private void printPublications(long orderId) {
        jdbc.sql("""
                        SELECT listener_id, status FROM event_publication
                        WHERE serialized_event::jsonb ->> 'orderId' = ? ORDER BY listener_id""")
                .param(String.valueOf(orderId))
                .query((rs, row) -> shortListener(rs.getString("listener_id")) + " → " + rs.getString("status"))
                .list()
                .forEach(line -> print("publication: " + line));
    }

    private String inventoryStatus(long orderId) {
        return jdbc.sql("""
                        SELECT status FROM event_publication
                        WHERE listener_id LIKE '%StockReservations%' AND serialized_event::jsonb ->> 'orderId' = ?""")
                .param(String.valueOf(orderId))
                .query(String.class)
                .optional()
                .orElse("");
    }

    /** "com.springbootedu.modulith.inventory.StockReservations.on(…)" → "inventory.StockReservations.on" */
    private static String shortListener(String listenerId) {
        String withoutParameters = listenerId.replaceAll("\\(.*\\)", "");
        return withoutParameters.replace("com.springbootedu.modulith.", "")
                .replace("org.springframework.modulith.events.support.", "");
    }

    /** The listeners run asynchronously: poll for a few seconds. */
    private static void waitUntil(BooleanSupplier condition) throws InterruptedException {
        for (int i = 0; i < 50 && !condition.getAsBoolean(); i++) {
            Thread.sleep(100);
        }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }

    private static void print(Object line) {
        System.out.println("  " + line);
    }
}
