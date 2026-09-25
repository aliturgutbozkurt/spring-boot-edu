package com.springbootedu.capstone.catalog.stock;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * What an order reserved; the order reference makes ReserveStock idempotent (a retry finds this document).
 */
@Document("reservations")
public record Reservation(@Id String orderRef, List<Line> lines, Status status) {

    public enum Status { RESERVED, RELEASED }

    public record Line(String isbn, String title, int quantity, BigDecimal unitPrice) {
    }

    public Reservation released() {
        return new Reservation(orderRef, lines, Status.RELEASED);
    }
}
