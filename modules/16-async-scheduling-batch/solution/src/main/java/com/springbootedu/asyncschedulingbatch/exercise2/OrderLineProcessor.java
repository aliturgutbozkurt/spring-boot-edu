package com.springbootedu.asyncschedulingbatch.exercise2;

import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Given: validates a line; throws InvalidOrderLineException for a line that cannot be imported.
 */
@Component
public class OrderLineProcessor implements ItemProcessor<OrderLine, ImportedOrder> {

    @Override
    public ImportedOrder process(OrderLine line) {
        if (line.isbn() == null || !line.isbn().matches("\\d{13}")) {
            throw new InvalidOrderLineException(line.orderId() + ": invalid ISBN");
        }
        int quantity;
        try {
            quantity = Integer.parseInt(line.quantity().trim());
        } catch (NumberFormatException e) {
            throw new InvalidOrderLineException(line.orderId() + ": quantity is not a number");
        }
        if (quantity <= 0) {
            throw new InvalidOrderLineException(line.orderId() + ": quantity must be positive");
        }
        return new ImportedOrder(line.orderId(), line.isbn(), quantity);
    }
}
