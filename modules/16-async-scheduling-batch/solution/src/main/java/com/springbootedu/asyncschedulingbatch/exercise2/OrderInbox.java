package com.springbootedu.asyncschedulingbatch.exercise2;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

/**
 * Given: where imported orders end up (in memory, so the exercise needs no database).
 */
@Component
public class OrderInbox implements ItemWriter<ImportedOrder> {

    private final List<ImportedOrder> orders = new CopyOnWriteArrayList<>();

    @Override
    public void write(Chunk<? extends ImportedOrder> chunk) {
        orders.addAll(chunk.getItems());
    }

    public List<ImportedOrder> orders() {
        return List.copyOf(orders);
    }

    public void clear() {
        orders.clear();
    }
}
