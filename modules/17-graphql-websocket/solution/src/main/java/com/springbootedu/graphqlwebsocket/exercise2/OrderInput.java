package com.springbootedu.graphqlwebsocket.exercise2;

import java.util.List;

/**
 * The schema's {@code input OrderInput}; the nested list is bound to {@link OrderLineInput} records.
 */
public record OrderInput(String customerId, List<OrderLineInput> lines) {
}
