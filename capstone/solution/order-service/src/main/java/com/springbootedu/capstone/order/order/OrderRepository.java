package com.springbootedu.capstone.order.order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByCustomerIdOrderByPlacedAtDesc(String customerId);

    Optional<Order> findByIdAndCustomerId(UUID id, String customerId);   // another customer's order is "not found"

    Optional<Order> findByCustomerIdAndIdempotencyKey(String customerId, String idempotencyKey);   // exercise 1
}
