package io.axoniq.demo.hotel.booking.util;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.extensions.reactor.messaging.ReactorMessageDispatchInterceptor;
import org.axonframework.messaging.Message;
import reactor.core.publisher.Mono;

@Slf4j
public class LoggingReactorMessageDispatchInterceptor<M extends Message<?>>
        implements ReactorMessageDispatchInterceptor<M> {

    @SuppressWarnings("unchecked")
    @Override
    public Mono<M> intercept(Mono<M> monoMessage) {
        return monoMessage.doOnNext(message -> log
                .info("Dispatched message: [{}]", message.getPayloadType().getSimpleName()));
    }
}
