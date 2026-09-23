package com.springbootedu.datajpapostgres.exercise2;

import com.springbootedu.datajpapostgres.exercise1.PurchaseOrder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Exercise 2 — orders of a customer, with and without the N+1 problem, and a report query.
 */
public interface OrderRepository extends JpaRepository<PurchaseOrder, Long>, JpaSpecificationExecutor<PurchaseOrder> {

    List<PurchaseOrder> findByCustomerEmail(String customerEmail);

    // TODO 2a: load the orders AND their lines with a single SQL statement
    List<PurchaseOrder> findWithLinesByCustomerEmail(String customerEmail);

    // TODO 2b: replace this default method with a JPQL query that returns one CustomerTotal per customer
    //          (sum of quantity * unitPrice over all lines), highest total first — computed by the database
    default List<CustomerTotal> totalsPerCustomer() {
        throw new UnsupportedOperationException("TODO 2b");
    }
}
