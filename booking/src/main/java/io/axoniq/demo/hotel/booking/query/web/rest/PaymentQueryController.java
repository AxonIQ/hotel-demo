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

package io.axoniq.demo.hotel.booking.query.web.rest;

import io.axoniq.demo.hotel.booking.query.api.FindPayment;
import io.axoniq.demo.hotel.booking.query.api.FindPayments;
import io.axoniq.demo.hotel.booking.query.api.FindPaymentsForAccount;
import io.axoniq.demo.hotel.booking.query.api.PaymentResponseData;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
public class PaymentQueryController {

    private final ReactorQueryGateway reactorQueryGateway;

    public PaymentQueryController(ReactorQueryGateway reactorQueryGateway) {
        this.reactorQueryGateway = reactorQueryGateway;
    }

    @GetMapping(path = "/payments")
    public Mono<List<PaymentResponseData>> all() {
        return reactorQueryGateway.query(new FindPayments(), ResponseTypes.multipleInstancesOf(PaymentResponseData.class));
    }

    @GetMapping(path = "/payments/{paymentId}")
    public Mono<PaymentResponseData> payment(@PathVariable UUID paymentId) {
        return reactorQueryGateway.query(new FindPayment(paymentId), ResponseTypes.instanceOf(
                PaymentResponseData.class));
    }

    @GetMapping(path = "/payments/account/{accountId}")
    public Mono<List<PaymentResponseData>> paymentsForAccount(@PathVariable UUID accountId) {
        return reactorQueryGateway.query(new FindPaymentsForAccount(accountId), ResponseTypes.multipleInstancesOf(PaymentResponseData.class));
    }
}
