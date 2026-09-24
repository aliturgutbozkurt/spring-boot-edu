package com.springbootedu.graphqlwebsocket.exercise2;

import java.util.List;

/**
 * The schema type {@code Order}; {@code total} is not a field here, it is computed by the controller.
 */
public record PlacedOrder(long id, String customerId, List<PlacedLine> lines) {
}
