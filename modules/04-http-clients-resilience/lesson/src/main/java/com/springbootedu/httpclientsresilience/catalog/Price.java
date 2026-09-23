package com.springbootedu.httpclientsresilience.catalog;

import java.math.BigDecimal;

/**
 * A price from the remote catalog.
 */
public record Price(BigDecimal amount, String currency) {
}
