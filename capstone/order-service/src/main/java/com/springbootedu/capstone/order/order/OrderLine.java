package com.springbootedu.capstone.order.order;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

/**
 * One line of an order (table order_lines). Title and price are copied from the catalog at order time:
 * a later price change must not change a placed order.
 */
@Embeddable
public record OrderLine(String isbn, String title, int quantity, @Column(name = "unit_price") BigDecimal unitPrice) {

    BigDecimal subtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
