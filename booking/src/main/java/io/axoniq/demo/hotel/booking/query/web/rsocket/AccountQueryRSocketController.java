package io.axoniq.demo.hotel.booking.query.web.rsocket;

import io.axoniq.demo.hotel.booking.query.api.AccountResponseData;
import io.axoniq.demo.hotel.booking.query.api.FindAccount;
import io.axoniq.demo.hotel.booking.query.api.FindAccounts;
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
public class AccountQueryRSocketController {

    private final ReactorQueryGateway reactorQueryGateway;

    public AccountQueryRSocketController(ReactorQueryGateway reactorQueryGateway) {
        this.reactorQueryGateway = reactorQueryGateway;
    }

    // Request-Response
    @MessageMapping("accounts.all")
    public Mono<List<AccountResponseData>> all() {
        return reactorQueryGateway.query(new FindAccounts(),
                                         ResponseTypes.multipleInstancesOf(AccountResponseData.class));
    }

    // Request-Stream
    @MessageMapping("accounts.all")
    public Flux<AccountResponseData> all_subscribe() {
        return reactorQueryGateway
                .subscriptionQueryMany(new FindAccounts(), AccountResponseData.class);
    }

    //#################################################################################################################

    // Request-Response
    @MessageMapping("accounts.{accountId}.get")
    public Mono<AccountResponseData> account(@DestinationVariable UUID accountId) {
        return reactorQueryGateway.query(new FindAccount(accountId),
                                         ResponseTypes.instanceOf(AccountResponseData.class));
    }

    // Request-Stream
    @MessageMapping("accounts.{accountId}.get")
    public Flux<AccountResponseData> account_subscribe(@DestinationVariable UUID accountId) {
        return reactorQueryGateway
                .subscriptionQueryMany(new FindAccount(accountId), AccountResponseData.class);
    }
}
