package com.springbootedu.grpc.catalog;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.grpc.catalog.v1.BookCatalogGrpc;
import com.springbootedu.grpc.catalog.v1.GetBookRequest;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.grpc.test.autoconfigure.AutoConfigureTestGrpcTransport;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Lesson 3.5 — a global server interceptor sees every call with its final status.
 */
@SpringBootTest(properties = "bookstore.tour.enabled=false")
@AutoConfigureTestGrpcTransport
class LoggingInterceptorTest {

    @Autowired
    BookCatalogGrpc.BookCatalogBlockingStub catalog;

    @Autowired
    CallLoggingInterceptor calls;

    @Test
    void everyCallIsRecordedWithItsStatus() {
        calls.clear();
        catalog.getBook(GetBookRequest.newBuilder().setIsbn("9780134685991").build());
        try {
            catalog.getBook(GetBookRequest.newBuilder().setIsbn("0000000000000").build());
        } catch (StatusRuntimeException expected) {
            // NOT_FOUND
        }

        assertThat(calls.recentCalls()).containsExactly(
                "bookstore.catalog.v1.BookCatalog/GetBook OK",
                "bookstore.catalog.v1.BookCatalog/GetBook NOT_FOUND");
    }
}
