package com.springbootedu.setupmodernjava.exercise3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/**
 * Exercise 3 — an order state machine on sealed types.
 */
class Exercise3Test {

    @Test
    void happyPath() {
        OrderState state = new New();
        state = OrderStateMachine.next(state, new Pay(new BigDecimal("150.00")));
        assertThat(state).isEqualTo(new Paid(new BigDecimal("150.00")));

        state = OrderStateMachine.next(state, new Ship("TR123"));
        assertThat(state).isEqualTo(new Shipped("TR123"));

        state = OrderStateMachine.next(state, new Deliver());
        assertThat(state).isEqualTo(new Delivered());
    }

    @Test
    void newAndPaidOrdersCanBeCancelled() {
        assertThat(OrderStateMachine.next(new New(), new Cancel("müşteri vazgeçti")))
                .isEqualTo(new Cancelled("müşteri vazgeçti"));
        assertThat(OrderStateMachine.next(new Paid(BigDecimal.TEN), new Cancel("stok yok")))
                .isEqualTo(new Cancelled("stok yok"));
    }

    @Test
    void illegalTransitionsAreRejectedWithAClearMessage() {
        assertThatIllegalStateException().isThrownBy(() -> OrderStateMachine.next(new New(), new Ship("TR1")))
                .withMessage("Cannot Ship when New");
        assertThatIllegalStateException().isThrownBy(() -> OrderStateMachine.next(new Shipped("TR1"), new Cancel("geç")))
                .withMessage("Cannot Cancel when Shipped");
        assertThatIllegalStateException().isThrownBy(() -> OrderStateMachine.next(new Delivered(), new Deliver()))
                .withMessage("Cannot Deliver when Delivered");
    }

    @Test
    void paymentMustBePositive() {
        assertThatIllegalStateException().isThrownBy(() -> OrderStateMachine.next(new New(), new Pay(BigDecimal.ZERO)))
                .withMessage("Cannot Pay when New");
    }
}
