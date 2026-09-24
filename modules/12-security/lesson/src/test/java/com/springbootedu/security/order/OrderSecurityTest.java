package com.springbootedu.security.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import com.springbootedu.security.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

/**
 * Lesson 3.4 — method security: rules on the service, not only on URLs.
 */
@SpringBootTest(properties = "bookstore.tour.enabled=false")
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class OrderSecurityTest {

    @Autowired
    MockMvcTester mvc;

    @Autowired
    OrderService orders;

    @Test
    void aCustomerSeesTheirOwnOrder() {
        assertThat(mvc.get().uri("/api/orders/1").with(user("ada").roles("CUSTOMER")))
                .hasStatusOk().bodyJson().extractingPath("$.customer").isEqualTo("ada");
    }

    @Test
    void aCustomerCannotSeeSomeoneElsesOrder() {
        assertThat(mvc.get().uri("/api/orders/2").with(user("ada").roles("CUSTOMER"))).hasStatus(403);
    }

    @Test
    void anAdminSeesEveryOrder() {
        assertThat(mvc.get().uri("/api/orders/2").with(user("admin").roles("ADMIN"))).hasStatusOk();
        assertThat(mvc.get().uri("/api/orders").with(user("admin").roles("ADMIN"))).hasStatusOk();
    }

    @Test
    void onlyAnAdminMayListAllOrders() {
        assertThat(mvc.get().uri("/api/orders").with(user("ada").roles("CUSTOMER"))).hasStatus(403);
    }

    @Test
    @WithMockUser(username = "ada", roles = "CUSTOMER")
    void theRuleIsEnforcedOnTheServiceItselfNotOnlyOnTheUrl() {
        assertThatThrownBy(() -> orders.findAll()).isInstanceOf(AuthorizationDeniedException.class);
    }
}
