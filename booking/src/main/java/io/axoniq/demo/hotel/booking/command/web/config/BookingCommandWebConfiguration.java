package io.axoniq.demo.hotel.booking.command.web.config;

import io.axoniq.demo.hotel.booking.util.LoggingReactorMessageDispatchInterceptor;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookingCommandWebConfiguration {

    /***********************************************************/
    /* Register a dispatch interceptors on the command gateway */
    /***********************************************************/

    @Autowired
    public void reactiveCommandGatewayConfiguration(ReactorCommandGateway reactorCommandGateway) {
        reactorCommandGateway.registerDispatchInterceptor(new LoggingReactorMessageDispatchInterceptor<>());
    }
}
