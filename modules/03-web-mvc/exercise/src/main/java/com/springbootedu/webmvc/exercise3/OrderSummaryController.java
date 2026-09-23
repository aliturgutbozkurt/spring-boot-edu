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

    // TODO 3c: make this mapping version 1
    @GetMapping("/{id}")
    public OrderSummaryV1 getV1(@PathVariable long id) {
        return OrderSummaryV1.from(find(id));
    }

    // TODO 3d: add a version 2 mapping of the same URL that returns an OrderSummaryV2 record:
    //          {"id", "total": {"amount", "currency": "TRY"}, "status": {"code", "label"}, "lines": [...]}
    //          (create the OrderSummaryV2 record in this package)

    private static OrderSummary find(long id) {
        return new OrderSummary(id, new BigDecimal("145.00"), OrderSummary.Status.PAID,
                List.of(new OrderSummary.Line("Effective Java", 1), new OrderSummary.Line("Java Puzzlers", 1)));
    }
}
