package com.springbootedu.capstone.order.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * POST /api/orders — the client sends only ISBNs and quantities; titles and prices come from the catalog.
 */
public record PlaceOrderRequest(@NotEmpty List<@Valid Line> lines) {

    public record Line(@NotBlank String isbn, @Min(1) @Max(99) int quantity) {
    }
}
