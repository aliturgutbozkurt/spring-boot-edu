package com.springbootedu.testing.payment;

import java.math.BigDecimal;

/**
 * Lesson 3.6 — what we send to the payment service.
 */
public record PaymentRequest(String reference, BigDecimal amount) {
}
