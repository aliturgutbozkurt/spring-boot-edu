package com.springbootedu.grpc.exercise2;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.grpc.exercises.v1.BookServiceGrpc;
import com.springbootedu.grpc.exercises.v1.OrderLine;
import com.springbootedu.grpc.exercises.v1.OrderSummary;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.grpc.test.autoconfigure.AutoConfigureTestGrpcTransport;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.grpc.client.ImportGrpcClients;

@SpringBootTest
@AutoConfigureTestGrpcTransport
@ImportGrpcClients(types = BookServiceGrpc.BookServiceStub.class)
class Exercise2Test {

    @Autowired
    BookServiceGrpc.BookServiceStub books;

    @Test
    void manyOrderLinesGiveOneSummary() throws Exception {
        var summary = new CompletableFuture<OrderSummary>();
        StreamObserver<OrderLine> lines = books.placeOrders(new StreamObserver<>() {
            @Override
            public void onNext(OrderSummary value) {
                summary.complete(value);
            }

            @Override
            public void onError(Throwable error) {
                summary.completeExceptionally(error);
            }

            @Override
            public void onCompleted() {
            }
        });

        lines.onNext(line("9780134685991", 2));        // 2 × 89.90
        lines.onNext(line("0000000000000", 1));        // unknown book
        lines.onNext(line("9781617297571", 1));        // 1 × 95.00
        lines.onNext(line("9780321336781", 0));        // no copies
        lines.onCompleted();

        OrderSummary result = summary.get(5, TimeUnit.SECONDS);
        assertThat(result.getAcceptedLines()).isEqualTo(2);
        assertThat(result.getRejectedLines()).isEqualTo(2);
        assertThat(result.getTotalCents()).isEqualTo(27480);
    }

    private static OrderLine line(String isbn, int quantity) {
        return OrderLine.newBuilder().setIsbn(isbn).setQuantity(quantity).build();
    }
}
