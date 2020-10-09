/*
 * Copyright (c) 2020-2020. AxonIQ
 *
 * Licensed under the Apache License, Version 2.0 (the &quot;License&quot;)
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
