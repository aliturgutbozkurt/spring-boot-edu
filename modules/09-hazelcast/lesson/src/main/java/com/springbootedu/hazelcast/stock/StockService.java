package com.springbootedu.hazelcast.stock;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

/**
 * Lesson 3.5 — "read, check, write" on shared stock is only safe when no one else can interleave.
 */
@Service
public class StockService {

    private final IMap<String, Integer> stock;

    public StockService(HazelcastInstance hazelcast) {
        this.stock = hazelcast.getMap("stock");
    }

    public void setAvailable(String isbn, int quantity) {
        stock.set(isbn, quantity);
    }

    public int available(String isbn) {
        return Objects.requireNonNullElse(stock.get(isbn), 0);
    }

    // tag::key-lock[]
    public boolean reserveWithLock(String isbn, int quantity) {
        stock.lock(isbn);                                             // cluster-wide lock on this key only
        try {
            int available = available(isbn);
            if (available < quantity) {
                return false;
            }
            stock.set(isbn, available - quantity);
            return true;
        } finally {
            stock.unlock(isbn);                                       // always release, even after an exception
        }
    }

    public boolean tryReserve(String isbn, int quantity) throws InterruptedException {
        if (!stock.tryLock(isbn, 100, TimeUnit.MILLISECONDS)) {       // do not wait forever for a busy key
            return false;
        }
        try {
            int available = available(isbn);
            if (available < quantity) {
                return false;
            }
            stock.set(isbn, available - quantity);
            return true;
        } finally {
            stock.unlock(isbn);
        }
    }
    // end::key-lock[]

    // tag::use-entry-processor[]
    public boolean reserveWithEntryProcessor(String isbn, int quantity) {
        return Boolean.TRUE.equals(stock.executeOnKey(isbn, new ReserveStock(quantity)));   // one round trip
    }
    // end::use-entry-processor[]

    /**
     * Simulates another caller holding the lock of a key (used by the tests and the tour).
     */
    public void holdLockFor(String isbn, long millis) throws InterruptedException {
        stock.lock(isbn);
        try {
            Thread.sleep(millis);
        } finally {
            stock.unlock(isbn);
        }
    }
}
