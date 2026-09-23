package com.springbootedu.datajpapostgres.exercise3;

import com.springbootedu.datajpapostgres.exercise2.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Exercise 3 — combines only the criteria that are set.
 */
@Service
@Transactional(readOnly = true)
public class OrderSearch {

    private final OrderRepository orders;

    public OrderSearch(OrderRepository orders) {
        this.orders = orders;
    }

    public Page<OrderSummary> search(OrderFilter filter, Pageable pageable) {
        // TODO 3c: combine only the criteria that are set (all of them must match),
        //          run the query with paging and map every order to an OrderSummary
        throw new UnsupportedOperationException("TODO 3c — " + orders);
    }
}
