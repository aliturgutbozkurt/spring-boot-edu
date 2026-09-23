package com.springbootedu.datajdbcpostgres.order;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

/**
 * Lesson 3.4 — one repository per aggregate root. Spring Data generates the implementation.
 */
// tag::repository[]
public interface PurchaseOrderRepository extends ListCrudRepository<PurchaseOrder, Long> {

    List<PurchaseOrder> findByCustomerEmail(String customerEmail);      // derived from the method name

    @Query("""
            select po.id from purchase_order po
            join order_line ol on ol.purchase_order = po.id
            group by po.id
            having sum(ol.quantity * ol.unit_price) > :minimum""")
    List<Long> findIdsWithTotalAbove(BigDecimal minimum);               // hand-written SQL when needed
}
// end::repository[]
