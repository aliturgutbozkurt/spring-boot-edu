package com.springbootedu.springcloud.order;

import com.springbootedu.springcloud.catalog.Book;
import com.springbootedu.springcloud.catalog.CatalogFeignClient;
import com.springbootedu.springcloud.catalog.CatalogHttpClient;
import java.math.BigDecimal;
import java.util.function.Supplier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;

/**
 * Lessons 3.2–3.5 — prices an order with the catalog; when the catalog fails, the circuit breaker answers.
 */
@Service
public class OrderService {

    private final CatalogHttpClient httpClient;
    private final CatalogFeignClient feignClient;
    private final CircuitBreaker catalogBreaker;
    private final OrderLimits limits;

    OrderService(CatalogHttpClient httpClient, CatalogFeignClient feignClient,
                 CircuitBreakerFactory<?, ?> circuitBreakers, OrderLimits limits) {
        this.httpClient = httpClient;
        this.feignClient = feignClient;
        this.catalogBreaker = circuitBreakers.create("catalog");
        this.limits = limits;
    }

    // tag::circuit-breaker[]
    public OrderResponse place(OrderRequest request, String client) {
        if (request.quantity() > limits.maxQuantity()) {
            throw new IllegalArgumentException("At most " + limits.maxQuantity() + " copies per order");
        }
        Supplier<Book> call = "feign".equals(client)
                ? () -> feignClient.find(request.isbn())
                : () -> httpClient.find(request.isbn());
        return catalogBreaker.run(
                () -> priced(request, call.get(), client),
                error -> pending(request, client));        // fallback: failure, timeout, or the breaker is open
    }
    // end::circuit-breaker[]

    private static OrderResponse priced(OrderRequest request, Book book, String client) {
        BigDecimal total = book.price().multiply(BigDecimal.valueOf(request.quantity()));
        return new OrderResponse(request.isbn(), request.quantity(), total, OrderResponse.Status.PRICED,
                book.servedBy(), client);
    }

    private static OrderResponse pending(OrderRequest request, String client) {
        return new OrderResponse(request.isbn(), request.quantity(), null, OrderResponse.Status.PENDING,
                "fallback", client);
    }
}
