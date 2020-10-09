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
