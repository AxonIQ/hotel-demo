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

import io.axoniq.demo.hotel.booking.command.api.RegisterAccountCommand;
import io.axoniq.demo.hotel.booking.command.web.api.AccountRequestData;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Controller
public class AccountCommandRSocketController {

    private final ReactorCommandGateway reactorCommandGateway;

    public AccountCommandRSocketController(ReactorCommandGateway reactorCommandGateway) {
        this.reactorCommandGateway = reactorCommandGateway;
    }

    @MessageMapping("accounts.register")
    public Mono<UUID> register(AccountRequestData accountRequestData) {
        return reactorCommandGateway.send(new RegisterAccountCommand(UUID.randomUUID(),
                                                                     accountRequestData.getUserName(),
                                                                     accountRequestData.getPassword()));
    }
}
