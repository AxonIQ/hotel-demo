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
