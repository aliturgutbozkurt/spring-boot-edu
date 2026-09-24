package com.springbootedu.testing.order;

import static com.springbootedu.testing.fixtures.TestBooks.aBook;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.springbootedu.testing.audit.AuditLog;
import com.springbootedu.testing.book.Book;
import com.springbootedu.testing.book.BookNotFoundException;
import com.springbootedu.testing.book.BookRepository;
import com.springbootedu.testing.book.OutOfStockException;
import com.springbootedu.testing.payment.PaymentClient;
import com.springbootedu.testing.payment.PaymentReceipt;
import com.springbootedu.testing.payment.PaymentRequest;
import com.springbootedu.testing.pricing.PriceCalculator;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Lesson 3.2 — a unit test with Mockito: the collaborators are mocks, still no Spring context.
 */
// tag::mockito[]
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    BookRepository books;

    @Mock
    PaymentClient payments;

    @Mock
    AuditLog audit;

    @Captor
    ArgumentCaptor<PaymentRequest> paymentRequest;

    private OrderService service() {
        return new OrderService(books, new PriceCalculator(), payments, audit);   // the real calculator
    }

    @Test
    void chargesTheDiscountedTotalAndLowersTheStock() {
        Book book = aBook().isbn("9780134685991").price("10.00").stock(8).build();
        given(books.findByIsbn("9780134685991")).willReturn(Optional.of(book));
        given(payments.charge(any())).willReturn(new PaymentReceipt("pay-1", "PAID"));

        OrderConfirmation confirmation = service().placeOrder("9780134685991", 5);

        then(payments).should().charge(paymentRequest.capture());
        assertThat(paymentRequest.getValue().amount()).isEqualByComparingTo("47.50");
        assertThat(confirmation.paymentId()).isEqualTo("pay-1");
        assertThat(book.getStock()).isEqualTo(3);
    }
    // end::mockito[]

    @Test
    void anUnknownBookIsReportedAndNothingIsCharged() {
        given(books.findByIsbn("unknown")).willReturn(Optional.empty());

        assertThatExceptionOfType(BookNotFoundException.class).isThrownBy(() -> service().placeOrder("unknown", 1));
        then(payments).should(never()).charge(any());
    }

    @Test
    void anOrderAboveTheStockIsRejectedBeforePayment() {
        given(books.findByIsbn("9780134685991")).willReturn(Optional.of(aBook().stock(2).build()));

        assertThatExceptionOfType(OutOfStockException.class).isThrownBy(() -> service().placeOrder("9780134685991", 3));
        then(payments).shouldHaveNoInteractions();
        then(audit).shouldHaveNoInteractions();
    }
}
