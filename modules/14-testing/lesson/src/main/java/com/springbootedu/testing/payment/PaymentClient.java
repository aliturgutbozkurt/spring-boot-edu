package com.springbootedu.testing.payment;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * Lesson 3.6 — an HTTP interface client (module 04). In tests it is replaced by a mock.
 */
@HttpExchange("/payments")
public interface PaymentClient {

    @PostExchange
    PaymentReceipt charge(@RequestBody PaymentRequest request);
}
