package com.springbootedu.datajpapostgres.exercise3;

import com.springbootedu.datajpapostgres.exercise1.PurchaseOrder;
import com.springbootedu.datajpapostgres.exercise2.OrderRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
        List<Specification<PurchaseOrder>> criteria = new ArrayList<>();
        if (filter.customerEmail() != null) {
            criteria.add(OrderSpecifications.customer(filter.customerEmail()));
        }
        if (filter.status() != null) {
            criteria.add(OrderSpecifications.status(filter.status()));
        }
        if (filter.createdAfter() != null) {
            criteria.add(OrderSpecifications.createdAfter(filter.createdAfter()));
        }
        if (filter.containsIsbn() != null) {
            criteria.add(OrderSpecifications.containsIsbn(filter.containsIsbn()));
        }
        return orders.findAll(Specification.allOf(criteria), pageable)
                .map(order -> new OrderSummary(Objects.requireNonNull(order.getId()), order.getCustomerEmail(),
                        order.getStatus()));
    }
}
