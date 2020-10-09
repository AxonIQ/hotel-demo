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

package io.axoniq.demo.hotel.booking.command.web.rest;

import io.axoniq.demo.hotel.booking.command.api.RegisterAccountCommand;
import io.axoniq.demo.hotel.booking.command.web.api.AccountRequestData;
import io.axoniq.demo.hotel.booking.query.api.AccountResponseData;
import io.axoniq.demo.hotel.booking.query.api.FindAccount;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.UUID;


@RestController
@CrossOrigin
public class AccountCommandController {

    public static final int TIMEOUT_SECONDS = 5;
    private final ReactorCommandGateway reactorCommandGateway;
    private final ReactorQueryGateway reactorQueryGateway;

    public AccountCommandController(ReactorCommandGateway reactorCommandGateway,
                                    ReactorQueryGateway reactorQueryGateway) {
        this.reactorCommandGateway = reactorCommandGateway;
        this.reactorQueryGateway = reactorQueryGateway;
    }

    @PostMapping(path = "/accounts")
    public Mono<AccountResponseData> register(@RequestBody AccountRequestData accountRequestData) {
        UUID accountId = UUID.randomUUID();
        return reactorCommandGateway.send(new RegisterAccountCommand(accountId,
                                                                     accountRequestData.getUserName(),
                                                                     accountRequestData.getPassword()))
                                    .transform(objectMono -> Mono.zip(
                                            objectMono
                                                    .subscribeOn(Schedulers.parallel()),
                                            accountSubscriptionQuery(accountId)
                                                    .subscribeOn(Schedulers.parallel()))
                                                                 .map(Tuple2::getT2));
    }


    private Mono<AccountResponseData> accountSubscriptionQuery(UUID accountId) {
        Flux<AccountResponseData> queryResult = reactorQueryGateway.queryUpdates(new FindAccount(accountId),
                                                                                 ResponseTypes
                                                                                         .instanceOf(AccountResponseData.class));
        return queryResult
                .next()
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS));
    }
}
