package com.springbootedu.capstone.order.stock;

import java.math.BigDecimal;

/**
 * One reserved order line, with the title and price the catalog had at reservation time.
 */
public record ReservedBook(String isbn, String title, int quantity, BigDecimal unitPrice) {
}
