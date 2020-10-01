package io.axoniq.demo.hotel.booking.command.web.rest;

import io.axoniq.demo.hotel.booking.command.api.PayCommand;
import io.axoniq.demo.hotel.booking.command.api.ProcessPaymentCommand;
import io.axoniq.demo.hotel.booking.command.web.api.PayRequestData;
import io.axoniq.demo.hotel.booking.query.api.FindPayment;
import io.axoniq.demo.hotel.booking.query.api.PaymentResponseData;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

@RestController
@CrossOrigin
public class PaymentCommandController {

    public static final int TIMEOUT_SECONDS = 5;

    private final ReactorCommandGateway reactorCommandGateway;
    private final ReactorQueryGateway reactorQueryGateway;


    public PaymentCommandController(ReactorCommandGateway reactorCommandGateway,
                                    ReactorQueryGateway reactorQueryGateway) {
        this.reactorCommandGateway = reactorCommandGateway;
        this.reactorQueryGateway = reactorQueryGateway;
    }

    private Mono<PaymentResponseData> subscribeToPaymentUpdates(UUID paymentId) {
        Flux<PaymentResponseData> queryResult = reactorQueryGateway
                .queryUpdates(new FindPayment(paymentId), ResponseTypes.instanceOf(PaymentResponseData.class));

        return queryResult.next().timeout(Duration.ofSeconds(TIMEOUT_SECONDS));
    }

    @PostMapping(path = "/payments")
    public Mono<UUID> pay(@RequestBody PayRequestData payRequestData) {
        UUID paymentId = UUID.randomUUID();
        return Mono.when(subscribeToPaymentUpdates(paymentId))
                   .and(reactorCommandGateway
                                .send(new PayCommand(paymentId,
                                                     payRequestData.getAccountId(),
                                                     payRequestData.getTotalAmount())))
                   .then(Mono.just(paymentId));
    }

    @PostMapping(path = "/payments/{paymentId}/processed")
    public Mono<UUID> process(@PathVariable UUID paymentId) {
        return Mono.when(subscribeToPaymentUpdates(paymentId))
                   .and(reactorCommandGateway.send(new ProcessPaymentCommand(paymentId)))
                   .then(Mono.just(paymentId));
    }
}
