package com.springbootedu.security.exercise2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * Exercise 2 — "customers see only their own orders", enforced on the service.
 */
@SpringBootTest
class Exercise2Test {

    @Autowired
    OrderQueries orders;

    @Test
    @WithMockUser(username = "ada", roles = "CUSTOMER")
    void aCustomerMayListTheirOwnOrders() {
        assertThat(orders.ordersOf("ada")).extracting(CustomerOrder::customer).containsOnly("ada");
    }

    @Test
    @WithMockUser(username = "ada", roles = "CUSTOMER")
    void aCustomerMayNotListSomeoneElsesOrders() {
        assertThatThrownBy(() -> orders.ordersOf("bob")).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void anAdminMayListAnyonesOrders() {
        assertThat(orders.ordersOf("bob")).isNotEmpty();
    }

    @Test
    @WithMockUser(username = "bob", roles = "CUSTOMER")
    void recentOrdersAreFilteredToTheCaller() {
        assertThat(orders.recentOrders()).isNotEmpty().extracting(CustomerOrder::customer).containsOnly("bob");
    }

    @Test
    @WithMockUser(username = "ada", roles = "CUSTOMER")
    void aSingleOrderIsOnlyVisibleToItsOwner() {
        assertThat(orders.find(1).customer()).isEqualTo("ada");
        assertThatThrownBy(() -> orders.find(2)).isInstanceOf(AccessDeniedException.class);
    }
}
