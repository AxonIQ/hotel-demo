package io.axoniq.demo.hotel.booking.command;

import io.axoniq.demo.hotel.booking.TestFactoryKt;
import io.axoniq.demo.hotel.booking.command.api.AccountRegisteredEvent;
import io.axoniq.demo.hotel.booking.command.api.PayCommand;
import io.axoniq.demo.hotel.booking.command.api.PaymentRequestedEvent;
import io.axoniq.demo.hotel.booking.command.api.RegisterAccountCommand;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.UUID;

class AccountTest {

    private AggregateTestFixture<Account> testFixture;
    public static final String ACCOUNT_DOES_NOT_EXIST = "Account does not exist";


    @BeforeEach
    void setUp() {
        testFixture = new AggregateTestFixture<>(Account.class);
    }

    @Test
    void createAccountTest() {
        UUID accountId = UUID.randomUUID();
        RegisterAccountCommand registerAccountCommand = new RegisterAccountCommand(accountId,
                                                                                   TestFactoryKt.USER_NAME,
                                                                                   TestFactoryKt.PASS_WORD);
        AccountRegisteredEvent accountRegisteredEvent = new AccountRegisteredEvent(accountId,
                                                                                   TestFactoryKt.USER_NAME,
                                                                                   TestFactoryKt.PASS_WORD);

        testFixture.givenNoPriorActivity()
                   .when(registerAccountCommand)
                   .expectEvents(accountRegisteredEvent)
                   .expectSuccessfulHandlerExecution();
    }

    @Test
    void payTest() {
        UUID accountId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        AccountRegisteredEvent accountRegisteredEvent = new AccountRegisteredEvent(accountId,
                                                                                   TestFactoryKt.USER_NAME,
                                                                                   TestFactoryKt.PASS_WORD);
        PayCommand payCommand = new PayCommand(paymentId, accountId, BigDecimal.TEN);
        PaymentRequestedEvent paymentRequestedEvent = new PaymentRequestedEvent(paymentId, accountId, BigDecimal.TEN);

        testFixture.given(accountRegisteredEvent)
                   .when(payCommand)
                   .expectEvents(paymentRequestedEvent)
                   .expectSuccessfulHandlerExecution();
    }
}
