package com.springbootedu.webmvc.book;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.hibernate.validator.constraints.ISBN;
import org.jspecify.annotations.Nullable;

/**
 * Lesson 3.2 — the request body for POST and PUT. Validation rules live on the DTO, not in the controller.
 * Components are nullable because a client may leave them out; validation turns that into a 400.
 */
// tag::request[]
public record BookRequest(
        @NotBlank @ISBN @Nullable String isbn,                           // checks the ISBN checksum too
        @NotBlank @Size(max = 200) @Nullable String title,
        @NotEmpty @Nullable List<@NotBlank String> authors,             // constraints on list elements
        @NotNull @Positive @Digits(integer = 6, fraction = 2) @Nullable BigDecimal price,
        @NotNull @PastOrPresent @Nullable LocalDate publishedOn) {
}
// end::request[]
