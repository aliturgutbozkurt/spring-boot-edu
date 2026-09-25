package com.springbootedu.capstone.order.order;

import static com.springbootedu.capstone.order.FakeStockService.CLEAN_CODE;
import static com.springbootedu.capstone.order.FakeStockService.EFFECTIVE_JAVA;
import static com.springbootedu.capstone.order.FakeStockService.SLOW;
import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.capstone.order.FakeStockService;
import com.springbootedu.capstone.order.OrderTest;
import com.springbootedu.capstone.order.Tokens;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

/**
 * C.2 — placing and reading orders. Every test uses its own customer, because the database is shared.
 */
@OrderTest
class OrderApiTest {

    static final FakeStockService STOCK = OrderTest.FakeCatalog.STOCK;

    @Autowired
    MockMvcTester mvc;

    @Autowired
    JdbcClient jdbc;

    String customer = "customer-" + UUID.randomUUID();

    @BeforeEach
    void resetTheCatalog() {
        STOCK.reset();
    }

    @Test
    void anOrderTakesTitlesAndPricesFromTheCatalog() {
        MvcTestResult result = place(customer, line(EFFECTIVE_JAVA, 2), line(CLEAN_CODE, 1));

        assertThat(result).hasStatus(HttpStatus.CREATED).headers().containsHeader(HttpHeaders.LOCATION);
        assertThat(result).bodyJson().extractingPath("$.total").convertTo(BigDecimal.class)
                .satisfies(total -> assertThat(total).isEqualByComparingTo("120.50"));
        assertThat(result).bodyJson().extractingPath("$.lines[0].title").isEqualTo("Effective Java");
        assertThat(result).bodyJson().extractingPath("$.customerId").isEqualTo(customer);
        assertThat(STOCK.reserved).hasSize(1);
    }

    @Test
    void theCustomersTokenIsForwardedToTheCatalog() {
        String token = Tokens.bearer(customer, "USER");

        assertThat(mvc.post().uri("/api/orders").header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON).content(body(line(EFFECTIVE_JAVA, 1))))
                .hasStatus(HttpStatus.CREATED);
        assertThat(STOCK.lastAuthorization).isEqualTo(token);
    }

    @Test
    void theOrderAndItsEventAreWrittenTogether() {
        String id = orderId(place(customer, line(EFFECTIVE_JAVA, 1)));

        String payload = jdbc.sql("SELECT payload FROM outbox WHERE event_key = ? AND topic = 'bookstore.orders'")
                .param(id).query(String.class).single();
        assertThat(payload).contains("\"orderId\":\"" + id + "\"", "\"customerId\":\"" + customer + "\"");
    }

    @Test
    void notEnoughStockIsAConflict() {
        MvcTestResult result = place(customer, line(EFFECTIVE_JAVA, 6));

        assertThat(result).hasStatus(HttpStatus.CONFLICT)
                .bodyJson().extractingPath("$.detail").asString().contains("only 5 left");
        assertThat(ordersOf(customer)).isZero();
    }

    @Test
    void anUnknownBookCannotBeOrdered() {
        assertThat(place(customer, line("9999999999999", 1))).hasStatus(HttpStatus.UNPROCESSABLE_CONTENT);
    }

    @Test
    void aSlowCatalogEndsInServiceUnavailable() {
        assertThat(place(customer, line(SLOW, 1))).hasStatus(HttpStatus.SERVICE_UNAVAILABLE);   // deadline 500 ms
        assertThat(ordersOf(customer)).isZero();
    }

    @Test
    void whenSavingFailsTheReservationIsReleased() {
        String tooLong = "x".repeat(101);                       // customer_id is VARCHAR(100)

        assertThat(place(tooLong, line(EFFECTIVE_JAVA, 1))).hasFailed()      // a 500 in a real server
                .failure().hasRootCauseInstanceOf(PSQLException.class);
        assertThat(STOCK.released).containsExactlyElementsOf(STOCK.reserved.keySet());
    }

    @Test
    void anOrderNeedsAtLeastOneLine() {
        assertThat(mvc.post().uri("/api/orders").header(HttpHeaders.AUTHORIZATION, Tokens.bearer(customer, "USER"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"lines\":[]}"))
                .hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(STOCK.reserved).isEmpty();
    }

    @Test
    void withoutATokenNothingHappens() {
        assertThat(mvc.post().uri("/api/orders").contentType(MediaType.APPLICATION_JSON)
                .content(body(line(EFFECTIVE_JAVA, 1))))
                .hasStatus(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void customersSeeOnlyTheirOwnOrders() {
        String id = orderId(place(customer, line(EFFECTIVE_JAVA, 1)));
        String other = "other-" + UUID.randomUUID();

        assertThat(get("/api/orders", customer)).hasStatusOk().bodyJson().extractingPath("$[*].id").asArray()
                .containsExactly(id);
        assertThat(get("/api/orders/" + id, customer)).hasStatusOk();
        assertThat(get("/api/orders", other)).hasStatusOk().bodyJson().extractingPath("$").asArray().isEmpty();
        assertThat(get("/api/orders/" + id, other)).hasStatus(HttpStatus.NOT_FOUND);
    }

    private MvcTestResult place(String customerId, String... lines) {
        return mvc.post().uri("/api/orders").header(HttpHeaders.AUTHORIZATION, Tokens.bearer(customerId, "USER"))
                .contentType(MediaType.APPLICATION_JSON).content(body(lines)).exchange();
    }

    private MvcTestResult get(String uri, String customerId) {
        return mvc.get().uri(uri).header(HttpHeaders.AUTHORIZATION, Tokens.bearer(customerId, "USER")).exchange();
    }

    private static String body(String... lines) {
        return "{\"lines\":[" + String.join(",", lines) + "]}";
    }

    private static String line(String isbn, int quantity) {
        return "{\"isbn\":\"" + isbn + "\",\"quantity\":" + quantity + "}";
    }

    private static String orderId(MvcTestResult result) {
        assertThat(result).hasStatus(HttpStatus.CREATED);
        String location = result.getResponse().getHeader(HttpHeaders.LOCATION);
        return location.substring(location.lastIndexOf('/') + 1);
    }

    private int ordersOf(String customerId) {
        return jdbc.sql("SELECT count(*) FROM orders WHERE customer_id = ?").param(customerId).query(Integer.class).single();
    }
}
