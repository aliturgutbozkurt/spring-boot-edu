package com.springbootedu.capstone.order.stock;

import com.springbootedu.capstone.contracts.stock.v1.ReleaseStockRequest;
import com.springbootedu.capstone.contracts.stock.v1.ReserveStockRequest;
import com.springbootedu.capstone.contracts.stock.v1.ReserveStockResponse;
import com.springbootedu.capstone.contracts.stock.v1.StockLine;
import com.springbootedu.capstone.contracts.stock.v1.StockServiceGrpc;
import io.grpc.StatusRuntimeException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.springframework.grpc.client.interceptor.security.BearerTokenAuthenticationInterceptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * ADR-2 — reserves stock in the catalog over gRPC. The call carries the customer's JWT (ADR-5) and a deadline;
 * gRPC status codes become exceptions of this service.
 */
@Component
public class StockClient {

    private final StockServiceGrpc.StockServiceBlockingStub stock;
    private final Duration deadline;

    StockClient(StockServiceGrpc.StockServiceBlockingStub stock, CatalogProperties catalog) {
        // the interceptor asks for the token on every call: it is the token of the current request
        this.stock = stock.withInterceptors(new BearerTokenAuthenticationInterceptor(
                (Supplier<String>) StockClient::currentToken));
        this.deadline = catalog.deadline();
    }

    /** Reserves the quantities (ISBN → quantity) under the order's ID; a retry with the same ID reserves nothing new. */
    // TODO Exercise 3: retry this call twice when the catalog is unavailable (CatalogUnavailableException) —
    //  but never when the stock is too small. Why is a retry safe here?
    public List<ReservedBook> reserve(String orderId, Map<String, Integer> quantities) {
        var request = ReserveStockRequest.newBuilder().setOrderRef(orderId);
        quantities.forEach((isbn, quantity) ->
                request.addLines(StockLine.newBuilder().setIsbn(isbn).setQuantity(quantity)));
        try {
            ReserveStockResponse response = withDeadline().reserveStock(request.build());
            return response.getLinesList().stream()
                    .map(line -> new ReservedBook(line.getIsbn(), line.getTitle(), line.getQuantity(),
                            BigDecimal.valueOf(line.getUnitPriceCents(), 2)))
                    .toList();
        } catch (StatusRuntimeException e) {
            throw translate(e);
        }
    }

    /** Gives the stock of an order back, e.g. when the order could not be saved. */
    public void release(String orderId) {
        try {
            withDeadline().releaseStock(ReleaseStockRequest.newBuilder().setOrderRef(orderId).build());
        } catch (StatusRuntimeException e) {
            throw translate(e);
        }
    }

    private StockServiceGrpc.StockServiceBlockingStub withDeadline() {
        return stock.withDeadlineAfter(deadline.toMillis(), TimeUnit.MILLISECONDS);   // a new deadline per call
    }

    private static RuntimeException translate(StatusRuntimeException e) {
        String message = e.getStatus().getDescription() != null ? e.getStatus().getDescription() : e.getMessage();
        return switch (e.getStatus().getCode()) {
            case FAILED_PRECONDITION -> new OutOfStockException(message, e);
            case NOT_FOUND -> new UnknownBookException(message, e);
            case UNAVAILABLE, DEADLINE_EXCEEDED -> new CatalogUnavailableException("the catalog is not available", e);
            default -> e;
        };
    }

    private static String currentToken() {
        if (SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken jwt) {
            return jwt.getToken().getTokenValue();
        }
        throw new IllegalStateException("a stock reservation needs an authenticated customer");
    }
}
