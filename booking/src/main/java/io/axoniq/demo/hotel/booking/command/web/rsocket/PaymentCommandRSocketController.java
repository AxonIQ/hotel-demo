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

package io.axoniq.demo.hotel.booking.command.web.rsocket;

import io.axoniq.demo.hotel.booking.command.api.PayCommand;
import io.axoniq.demo.hotel.booking.command.api.ProcessPaymentCommand;
import io.axoniq.demo.hotel.booking.command.web.api.PayRequestData;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Controller
public class PaymentCommandRSocketController {

    private final ReactorCommandGateway reactorCommandGateway;

    public PaymentCommandRSocketController(ReactorCommandGateway reactorCommandGateway) {
        this.reactorCommandGateway = reactorCommandGateway;
    }

    @MessageMapping("payments.pay")
    public Mono<UUID> pay(@Payload PayRequestData payRequestData) {
        return reactorCommandGateway.send(new PayCommand(UUID.randomUUID(),
                                                         payRequestData.getAccountId(),
                                                         payRequestData.getTotalAmount()));
    }

    @MessageMapping("payments.{paymentId}.process")
    public Mono<Void> process(UUID paymentId) {
        return reactorCommandGateway.send(new ProcessPaymentCommand(paymentId));
    }
}
