package com.springbootedu.capstone.catalog.book;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;

/**
 * The body of PUT /api/catalog/books/{isbn} (admins only).
 */
public record BookRequest(@NotBlank String title, @NotEmpty List<String> authors, String description,
                          @DecimalMin("0.01") BigDecimal price, @Min(0) int stock) {
}
