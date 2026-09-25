package com.springbootedu.capstone.catalog.stock;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.springbootedu.capstone.catalog.book.Book;
import com.springbootedu.capstone.catalog.book.BookRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

/**
 * ADR-6 — a reservation changes several documents (books and the reservation). A Hazelcast lock per ISBN makes
 * this safe with several catalog instances; locking the ISBNs in sorted order prevents deadlocks.
 */
@Service
public class StockReservations {

    private final BookRepository books;
    private final ReservationRepository reservations;
    private final IMap<String, String> locks;

    StockReservations(BookRepository books, ReservationRepository reservations, HazelcastInstance hazelcast) {
        this.books = books;
        this.reservations = reservations;
        this.locks = hazelcast.getMap("stock-locks");
    }

    public Reservation reserve(String orderRef, Map<String, Integer> quantities) {
        var existing = reservations.findById(orderRef);
        if (existing.isPresent()) {
            return existing.get();                                       // idempotent: a retry of the same order
        }
        Map<String, Integer> sorted = new TreeMap<>(quantities);         // always the same lock order
        List<String> locked = new ArrayList<>();
        try {
            sorted.keySet().forEach(isbn -> locked.add(lock(isbn)));
            List<Book> selected = sorted.entrySet().stream().map(entry -> available(entry.getKey(), entry.getValue())).toList();
            List<Reservation.Line> lines = new ArrayList<>();
            for (Book book : selected) {
                int quantity = sorted.get(book.isbn());
                books.save(book.withStock(book.stock() - quantity));
                lines.add(new Reservation.Line(book.isbn(), book.title(), quantity, book.price()));
            }
            return reservations.save(new Reservation(orderRef, lines, Reservation.Status.RESERVED));
        } finally {
            locked.forEach(locks::unlock);
        }
    }

    public boolean release(String orderRef) {
        var reservation = reservations.findById(orderRef).filter(r -> r.status() == Reservation.Status.RESERVED);
        if (reservation.isEmpty()) {
            return false;
        }
        for (Reservation.Line line : reservation.get().lines()) {
            lock(line.isbn());
            try {
                books.findById(line.isbn()).ifPresent(book -> books.save(book.withStock(book.stock() + line.quantity())));
            } finally {
                locks.unlock(line.isbn());
            }
        }
        reservations.save(reservation.get().released());
        return true;
    }

    private String lock(String isbn) {
        try {
            // lease 10 s: a crashed instance cannot block an ISBN forever
            if (!locks.tryLock(isbn, 5, TimeUnit.SECONDS, 10, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Stock of " + isbn + " is locked, try again");
            }
            return isbn;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    private Book available(String isbn, int quantity) {
        Book book = books.findById(isbn).orElseThrow(() -> new UnknownBookException(isbn));
        if (book.stock() < quantity) {
            throw new InsufficientStockException(isbn, quantity, book.stock());
        }
        return book;
    }
}
