package com.springbootedu.capstone.order.order;

import com.springbootedu.capstone.contracts.events.OrderPlaced;
import com.springbootedu.capstone.order.outbox.Outbox;
import com.springbootedu.capstone.order.stock.CatalogUnavailableException;
import com.springbootedu.capstone.order.stock.ReservedBook;
import com.springbootedu.capstone.order.stock.StockClient;
import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * The order flow of architecture section 3.1: reserve the stock (gRPC), then save the order and its
 * OrderPlaced event in one transaction. If saving fails, the reservation is given back.
 */
@Service
class OrderService {

    private final OrderRepository orders;
    private final Outbox outbox;
    private final StockClient stock;
    private final TransactionTemplate transactions;
    private final Clock clock;

    OrderService(OrderRepository orders, Outbox outbox, StockClient stock, TransactionTemplate transactions,
                 Clock clock) {
        this.orders = orders;
        this.outbox = outbox;
        this.stock = stock;
        this.transactions = transactions;
        this.clock = clock;
    }

    // tag::place-order[]
    OrderResponse place(String customerId, PlaceOrderRequest request) {
        UUID id = UUID.randomUUID();                            // also the idempotency key of the reservation
        Map<String, Integer> quantities = request.lines().stream().collect(Collectors.toMap(
                PlaceOrderRequest.Line::isbn, PlaceOrderRequest.Line::quantity, Integer::sum, LinkedHashMap::new));

        // the remote call runs OUTSIDE the transaction: no database connection is held while we wait
        List<ReservedBook> reserved = reserveOrGiveBack(id, quantities);
        try {
            return transactions.execute(status -> save(id, customerId, reserved));
        } catch (RuntimeException e) {
            releaseAfterFailure(id, e);                         // compensation: the catalog gets its copies back
            throw e;
        }
    }
    // end::place-order[]

    private List<ReservedBook> reserveOrGiveBack(UUID id, Map<String, Integer> quantities) {
        try {
            return stock.reserve(id.toString(), quantities);
        } catch (CatalogUnavailableException e) {
            // a call that timed out may still have reserved the stock in the catalog: give it back
            // (ReleaseStock is idempotent; if the catalog is really down, this fails too — see releaseAfterFailure)
            releaseAfterFailure(id, e);
            throw e;
        }
    }

    List<OrderResponse> ordersOf(String customerId) {
        return orders.findByCustomerIdOrderByPlacedAtDesc(customerId).stream().map(OrderResponse::from).toList();
    }

    OrderResponse find(String customerId, UUID id) {
        return orders.findByIdAndCustomerId(id, customerId).map(OrderResponse::from)
                .orElseThrow(() -> new OrderNotFoundException(id.toString()));
    }

    private OrderResponse save(UUID id, String customerId, List<ReservedBook> reserved) {
        List<OrderLine> lines = reserved.stream()
                .map(book -> new OrderLine(book.isbn(), book.title(), book.quantity(), book.unitPrice()))
                .toList();
        Order order = orders.save(new Order(id, customerId, lines, clock.instant()));
        outbox.add(OrderPlaced.TOPIC, id.toString(), placedEvent(order));
        return OrderResponse.from(order);
    }

    private static OrderPlaced placedEvent(Order order) {
        List<OrderPlaced.Line> lines = order.getLines().stream()
                .map(line -> new OrderPlaced.Line(line.isbn(), line.title(), line.quantity(), line.unitPrice()))
                .toList();
        return new OrderPlaced(order.getId().toString(), order.getCustomerId(), lines, order.getTotal(),
                order.getPlacedAt());
    }

    private void releaseAfterFailure(UUID id, RuntimeException failure) {
        try {
            stock.release(id.toString());
        } catch (RuntimeException releaseFailure) {
            failure.addSuppressed(releaseFailure);             // keep the original error; the reservation stays
        }
    }
}
