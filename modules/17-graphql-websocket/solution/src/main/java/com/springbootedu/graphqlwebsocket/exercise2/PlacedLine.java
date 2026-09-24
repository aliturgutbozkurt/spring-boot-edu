package com.springbootedu.graphqlwebsocket.exercise2;

import java.math.BigDecimal;

/**
 * The schema type {@code OrderLine}; {@code book} is resolved from the ISBN by the controller.
 */
public record PlacedLine(String isbn, int quantity, BigDecimal unitPrice) {
}
