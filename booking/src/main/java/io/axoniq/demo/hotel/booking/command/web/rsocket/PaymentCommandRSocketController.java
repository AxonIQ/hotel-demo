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
