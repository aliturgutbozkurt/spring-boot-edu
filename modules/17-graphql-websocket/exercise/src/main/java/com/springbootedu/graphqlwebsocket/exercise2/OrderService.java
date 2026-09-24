package com.springbootedu.graphqlwebsocket.exercise2;

import com.springbootedu.graphqlwebsocket.catalog.Book;
import com.springbootedu.graphqlwebsocket.catalog.Catalog;
import com.springbootedu.graphqlwebsocket.catalog.Inventory;
import com.springbootedu.graphqlwebsocket.catalog.OutOfStockException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Given: validates an order, takes the books from the inventory and returns the placed order.
 *
 * @throws InvalidOrderException no lines or a quantity below 1
 * @throws UnknownBookException an ISBN that is not in the catalog
 * @throws OutOfStockException not enough copies
 */
@Service
public class OrderService {

    private final Catalog catalog;
    private final Inventory inventory;
    private final AtomicLong ids = new AtomicLong();

    OrderService(Catalog catalog, Inventory inventory) {
        this.catalog = catalog;
        this.inventory = inventory;
    }

    public PlacedOrder place(OrderInput input) {
        if (input.lines().isEmpty()) {
            throw new InvalidOrderException("An order needs at least one line");
        }
        List<PlacedLine> lines = input.lines().stream().map(this::toPlacedLine).toList();
        Map<String, Integer> quantities = lines.stream()
                .collect(Collectors.toMap(PlacedLine::isbn, PlacedLine::quantity, Integer::sum));
        inventory.take(quantities);
        return new PlacedOrder(ids.incrementAndGet(), input.customerId(), lines);
    }

    private PlacedLine toPlacedLine(OrderLineInput line) {
        if (line.quantity() < 1) {
            throw new InvalidOrderException("Quantity must be at least 1, not " + line.quantity());
        }
        Book book = catalog.findBook(line.isbn()).orElseThrow(() -> new UnknownBookException(line.isbn()));
        return new PlacedLine(book.isbn(), line.quantity(), book.price());
    }
}
