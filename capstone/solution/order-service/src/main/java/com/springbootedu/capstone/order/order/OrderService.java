package com.springbootedu.capstone.order.order;

import com.springbootedu.capstone.contracts.events.OrderPlaced;
import com.springbootedu.capstone.order.outbox.Outbox;
import com.springbootedu.capstone.order.stock.ReservedBook;
import com.springbootedu.capstone.order.stock.StockClient;
import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * The order flow of architecture section 3.1: reserve the stock (gRPC), then save the order and its
 * OrderPlaced event in one transaction. If saving fails, the reservation is given back.
 */
@Service
class OrderService {

    /** The result of placing an order: {@code created} is false when an idempotency key found an earlier order. */
    record Placed(OrderResponse order, boolean created) {
    }

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

    Placed place(String customerId, @Nullable String idempotencyKey, PlaceOrderRequest request) {
        Optional<Order> earlier = findByKey(customerId, idempotencyKey);
        if (earlier.isPresent()) {
            return new Placed(OrderResponse.from(earlier.get()), false);  // a retry: answer, but do nothing
        }
        UUID id = UUID.randomUUID();                            // also the idempotency key of the reservation
        Map<String, Integer> quantities = request.lines().stream().collect(Collectors.toMap(
                PlaceOrderRequest.Line::isbn, PlaceOrderRequest.Line::quantity, Integer::sum, LinkedHashMap::new));

        // the remote call runs OUTSIDE the transaction: no database connection is held while we wait
        List<ReservedBook> reserved = stock.reserve(id.toString(), quantities);
        try {
            return new Placed(transactions.execute(status -> save(id, customerId, idempotencyKey, reserved)), true);
        } catch (DataIntegrityViolationException e) {
            releaseAfterFailure(id, e);
            // two requests with the same key at the same moment: the unique index lets only one win
            return findByKey(customerId, idempotencyKey).map(winner -> new Placed(OrderResponse.from(winner), false))
                    .orElseThrow(() -> e);
        } catch (RuntimeException e) {
            releaseAfterFailure(id, e);                         // compensation: the catalog gets its copies back
            throw e;
        }
    }

    private Optional<Order> findByKey(String customerId, @Nullable String idempotencyKey) {
        return idempotencyKey == null ? Optional.empty()
                : orders.findByCustomerIdAndIdempotencyKey(customerId, idempotencyKey);
    }

    void cancel(String customerId, UUID id) {
        Order order = orders.findByIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new OrderNotFoundException(id.toString()));
        if (order.getStatus() == Order.Status.CANCELLED) {
            throw new OrderAlreadyCancelledException(id.toString());
        }
        // first the stock: ReleaseStock is idempotent, so a DELETE retried after a failure below is harmless
        stock.release(id.toString());
        transactions.executeWithoutResult(status -> {
            Order current = orders.findByIdAndCustomerId(id, customerId).orElseThrow();
            current.cancel();                                   // saved at commit (dirty checking)
            outbox.add(OrderCancelled.TOPIC, id.toString(), new OrderCancelled(id.toString(), customerId,
                    eventLines(current), clock.instant()));
        });
    }

    List<OrderResponse> ordersOf(String customerId) {
        return orders.findByCustomerIdOrderByPlacedAtDesc(customerId).stream().map(OrderResponse::from).toList();
    }

    OrderResponse find(String customerId, UUID id) {
        return orders.findByIdAndCustomerId(id, customerId).map(OrderResponse::from)
                .orElseThrow(() -> new OrderNotFoundException(id.toString()));
    }

    private OrderResponse save(UUID id, String customerId, @Nullable String idempotencyKey, List<ReservedBook> reserved) {
        List<OrderLine> lines = reserved.stream()
                .map(book -> new OrderLine(book.isbn(), book.title(), book.quantity(), book.unitPrice()))
                .toList();
        Order order = orders.saveAndFlush(new Order(id, customerId, lines, clock.instant(), idempotencyKey));
        outbox.add(OrderPlaced.TOPIC, id.toString(), new OrderPlaced(order.getId().toString(), order.getCustomerId(),
                eventLines(order), order.getTotal(), order.getPlacedAt()));
        return OrderResponse.from(order);
    }

    private static List<OrderPlaced.Line> eventLines(Order order) {
        return order.getLines().stream()
                .map(line -> new OrderPlaced.Line(line.isbn(), line.title(), line.quantity(), line.unitPrice()))
                .toList();
    }

    private void releaseAfterFailure(UUID id, RuntimeException failure) {
        try {
            stock.release(id.toString());
        } catch (RuntimeException releaseFailure) {
            failure.addSuppressed(releaseFailure);             // keep the original error; the reservation stays
        }
    }
}
