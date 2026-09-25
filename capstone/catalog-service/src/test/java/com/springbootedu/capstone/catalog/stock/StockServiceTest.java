package com.springbootedu.capstone.catalog.stock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.springbootedu.capstone.catalog.CatalogTest;
import com.springbootedu.capstone.catalog.Tokens;
import com.springbootedu.capstone.catalog.book.Book;
import com.springbootedu.capstone.catalog.book.BookRepository;
import com.springbootedu.capstone.contracts.stock.v1.ReleaseStockRequest;
import com.springbootedu.capstone.contracts.stock.v1.ReserveStockRequest;
import com.springbootedu.capstone.contracts.stock.v1.ReserveStockResponse;
import com.springbootedu.capstone.contracts.stock.v1.StockLine;
import com.springbootedu.capstone.contracts.stock.v1.StockServiceGrpc;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.client.ImportGrpcClients;
import org.springframework.grpc.client.interceptor.security.BearerTokenAuthenticationInterceptor;

@CatalogTest
@ImportGrpcClients(types = StockServiceGrpc.StockServiceBlockingStub.class)
class StockServiceTest {

    StockServiceGrpc.StockServiceBlockingStub stock;

    @Autowired
    void stub(StockServiceGrpc.StockServiceBlockingStub stub) {         // calls carry the customer's token
        this.stock = stub.withInterceptors(new BearerTokenAuthenticationInterceptor(
                Tokens.bearer("ayse", "USER").substring("Bearer ".length())));
    }

    @Autowired
    BookRepository books;

    @Autowired
    ReservationRepository reservations;

    @BeforeEach
    void catalog() {
        books.deleteAll();
        reservations.deleteAll();
        books.save(book("9780134685991", "Effective Java", "89.90", 5));
        books.save(book("9781617297571", "Spring in Action", "95.00", 2));
    }

    @Test
    void aReservationTakesTheStockAndReturnsThePrices() {
        ReserveStockResponse response = stock.reserveStock(request("order-1", "9780134685991", 2, "9781617297571", 1));

        assertThat(response.getLinesList()).extracting(line -> line.getUnitPriceCents()).containsExactlyInAnyOrder(8990L, 9500L);
        assertThat(stockOf("9780134685991")).isEqualTo(3);
        assertThat(stockOf("9781617297571")).isEqualTo(1);
    }

    @Test
    void aRetryOfTheSameOrderReservesOnlyOnce() {
        stock.reserveStock(request("order-2", "9780134685991", 2));
        stock.reserveStock(request("order-2", "9780134685991", 2));

        assertThat(stockOf("9780134685991")).isEqualTo(3);
    }

    @Test
    void notEnoughStockReservesNothing() {
        assertThatThrownBy(() -> stock.reserveStock(request("order-3", "9780134685991", 1, "9781617297571", 3)))
                .isInstanceOfSatisfying(StatusRuntimeException.class,
                        e -> assertThat(e.getStatus().getCode()).isEqualTo(Status.Code.FAILED_PRECONDITION));
        assertThat(stockOf("9780134685991")).isEqualTo(5);                 // all or nothing
    }

    @Test
    void withoutATokenTheCallIsRejected(@Autowired StockServiceGrpc.StockServiceBlockingStub anonymous) {
        assertThatThrownBy(() -> anonymous.reserveStock(request("order-0", "9780134685991", 1)))
                .isInstanceOfSatisfying(StatusRuntimeException.class,
                        e -> assertThat(e.getStatus().getCode()).isEqualTo(Status.Code.UNAUTHENTICATED));
    }

    @Test
    void anUnknownBookIsNotFound() {
        assertThatThrownBy(() -> stock.reserveStock(request("order-4", "0000000000000", 1)))
                .isInstanceOfSatisfying(StatusRuntimeException.class,
                        e -> assertThat(e.getStatus().getCode()).isEqualTo(Status.Code.NOT_FOUND));
    }

    @Test
    void aReleaseGivesTheStockBack() {
        stock.reserveStock(request("order-5", "9780134685991", 4));

        assertThat(stock.releaseStock(ReleaseStockRequest.newBuilder().setOrderRef("order-5").build()).getReleased()).isTrue();
        assertThat(stockOf("9780134685991")).isEqualTo(5);
        assertThat(stock.releaseStock(ReleaseStockRequest.newBuilder().setOrderRef("order-5").build()).getReleased()).isFalse();
    }

    @Test
    void parallelOrdersNeverSellMoreThanTheStock() throws Exception {
        List<Callable<Boolean>> orders = new java.util.ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String ref = "parallel-" + i;
            orders.add(() -> {
                try {
                    stock.reserveStock(request(ref, "9780134685991", 1));
                    return true;
                } catch (StatusRuntimeException e) {
                    return false;
                }
            });
        }
        try (var executor = Executors.newFixedThreadPool(10)) {
            long succeeded = 0;
            for (Future<Boolean> result : executor.invokeAll(orders)) {
                succeeded += result.get() ? 1 : 0;
            }
            assertThat(succeeded).isEqualTo(5);                             // the stock was 5
        }
        assertThat(stockOf("9780134685991")).isZero();
    }

    private int stockOf(String isbn) {
        return books.findById(isbn).orElseThrow().stock();
    }

    private static ReserveStockRequest request(String orderRef, Object... isbnAndQuantity) {
        ReserveStockRequest.Builder request = ReserveStockRequest.newBuilder().setOrderRef(orderRef);
        for (int i = 0; i < isbnAndQuantity.length; i += 2) {
            request.addLines(StockLine.newBuilder().setIsbn((String) isbnAndQuantity[i]).setQuantity((Integer) isbnAndQuantity[i + 1]));
        }
        return request.build();
    }

    private static Book book(String isbn, String title, String price, int stock) {
        return new Book(isbn, title, List.of("Author"), "", new BigDecimal(price), stock, null);
    }
}
