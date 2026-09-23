package com.springbootedu.hazelcast.stock;

import com.hazelcast.map.EntryProcessor;
import java.util.Map;

/**
 * Lesson 3.5 — runs on the member that owns the key, one entry processor at a time per key: no lock needed.
 */
// tag::entry-processor[]
public record ReserveStock(int quantity) implements EntryProcessor<String, Integer, Boolean> {

    @Override
    public Boolean process(Map.Entry<String, Integer> entry) {
        Integer available = entry.getValue();
        if (available == null || available < quantity) {
            return false;
        }
        entry.setValue(available - quantity);                        // written back by Hazelcast
        return true;
    }
}
// end::entry-processor[]
