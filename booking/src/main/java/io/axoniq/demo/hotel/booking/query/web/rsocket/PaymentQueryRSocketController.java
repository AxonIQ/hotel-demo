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

package io.axoniq.demo.hotel.booking.query.web.rsocket;

import io.axoniq.demo.hotel.booking.query.api.FindPayment;
import io.axoniq.demo.hotel.booking.query.api.FindPayments;
import io.axoniq.demo.hotel.booking.query.api.FindPaymentsForAccount;
import io.axoniq.demo.hotel.booking.query.api.PaymentResponseData;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Controller
public class PaymentQueryRSocketController {

    private final ReactorQueryGateway reactorQueryGateway;

    public PaymentQueryRSocketController(ReactorQueryGateway reactorQueryGateway) {
        this.reactorQueryGateway = reactorQueryGateway;
    }

    // Request-Response
    @MessageMapping("payments.all")
    public Mono<List<PaymentResponseData>> all() {
        return reactorQueryGateway.query(new FindPayments(),
                                         ResponseTypes.multipleInstancesOf(PaymentResponseData.class));
    }

    // Request-Stream
    @MessageMapping("payments.all")
    public Flux<PaymentResponseData> all_subscribe() {
        return reactorQueryGateway
                .subscriptionQueryMany(new FindPayments(), PaymentResponseData.class);
    }

    //#################################################################################################################

    // Request-Response
    @MessageMapping("payments.{paymentId}.get")
    public Mono<PaymentResponseData> payment(@DestinationVariable UUID paymentId) {
        return reactorQueryGateway.query(new FindPayment(paymentId), ResponseTypes.instanceOf(
                PaymentResponseData.class));
    }

    // Request-Stream
    @MessageMapping("payments.{paymentId}.get")
    public Flux<PaymentResponseData> payment_subscribe(@DestinationVariable UUID paymentId) {
        return reactorQueryGateway
                .subscriptionQueryMany(new FindPayment(paymentId), PaymentResponseData.class);
    }

    //#################################################################################################################

    // Request-Response
    @MessageMapping("payments.account.{accountId}.get")
    public Mono<List<PaymentResponseData>> paymentsForAccount(@DestinationVariable UUID accountId) {
        return reactorQueryGateway.query(new FindPaymentsForAccount(accountId),
                                         ResponseTypes.multipleInstancesOf(PaymentResponseData.class));
    }

    // Request-Stream
    @MessageMapping("payments.account.{accountId}.get")
    public Flux<PaymentResponseData> paymentsForAccount_subscribe(@DestinationVariable UUID accountId) {
        return reactorQueryGateway
                .subscriptionQueryMany(new FindPaymentsForAccount(accountId), PaymentResponseData.class);
    }
}
