package io.axoniq.demo.hotel.booking.command;

import io.axoniq.demo.hotel.booking.command.api.PaymentRequestedEvent;
import io.axoniq.demo.hotel.booking.command.api.PaymentSucceededEvent;
import io.axoniq.demo.hotel.booking.command.api.ProcessPaymentCommand;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.UUID;

class PaymentTest {

    private AggregateTestFixture<Payment> testFixture;

    @BeforeEach
    void setUp() {
        testFixture = new AggregateTestFixture<>(Payment.class);
    }

    @Test
    void processPaymentTest() {
        UUID accountId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        PaymentRequestedEvent paymentRequestedEvent = new PaymentRequestedEvent(paymentId, accountId, BigDecimal.TEN);
        ProcessPaymentCommand processPaymentCommand = new ProcessPaymentCommand(paymentId);
        PaymentSucceededEvent paymentSucceededEvent = new PaymentSucceededEvent(paymentId);

        testFixture.given(paymentRequestedEvent)
                   .when(processPaymentCommand)
                   .expectEvents(paymentSucceededEvent)
                   .expectSuccessfulHandlerExecution();
    }
}
