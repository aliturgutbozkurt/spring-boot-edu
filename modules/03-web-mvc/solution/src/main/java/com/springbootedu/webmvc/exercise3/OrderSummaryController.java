package com.springbootedu.webmvc.exercise3;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exercise 3 — one URL, two response versions.
 */
@RestController
@RequestMapping("/api/order-summaries")
public class OrderSummaryController {

    @GetMapping(path = "/{id}", version = "1")
    public OrderSummaryV1 getV1(@PathVariable long id) {
        return OrderSummaryV1.from(find(id));
    }

    @GetMapping(path = "/{id}", version = "2")
    public OrderSummaryV2 getV2(@PathVariable long id) {
        return OrderSummaryV2.from(find(id));
    }

    private static OrderSummary find(long id) {
        return new OrderSummary(id, new BigDecimal("145.00"), OrderSummary.Status.PAID,
                List.of(new OrderSummary.Line("Effective Java", 1), new OrderSummary.Line("Java Puzzlers", 1)));
    }
}
