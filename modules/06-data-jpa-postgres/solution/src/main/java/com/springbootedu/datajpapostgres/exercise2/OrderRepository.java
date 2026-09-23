package com.springbootedu.datajpapostgres.exercise2;

import com.springbootedu.datajpapostgres.exercise1.PurchaseOrder;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Exercise 2 — orders of a customer, with and without the N+1 problem, and a report query.
 */
public interface OrderRepository extends JpaRepository<PurchaseOrder, Long>, JpaSpecificationExecutor<PurchaseOrder> {

    List<PurchaseOrder> findByCustomerEmail(String customerEmail);

    @EntityGraph(attributePaths = "lines")
    List<PurchaseOrder> findWithLinesByCustomerEmail(String customerEmail);

    @Query("""
            select new com.springbootedu.datajpapostgres.exercise2.CustomerTotal(
                       o.customerEmail, sum(l.quantity * l.unitPrice))
            from PurchaseOrder o join o.lines l
            group by o.customerEmail
            order by sum(l.quantity * l.unitPrice) desc""")
    List<CustomerTotal> totalsPerCustomer();
}
