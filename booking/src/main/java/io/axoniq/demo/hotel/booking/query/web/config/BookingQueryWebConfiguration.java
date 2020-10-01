package io.axoniq.demo.hotel.booking.query.web.config;

import io.axoniq.demo.hotel.booking.util.LoggingReactorMessageDispatchInterceptor;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookingQueryWebConfiguration {

    /*********************************************************/
    /* Register a dispatch interceptors on the query gateway */
    /*********************************************************/

    @Autowired
    public void reactiveCommandGatewayConfiguration(ReactorQueryGateway reactorQueryGateway) {
        reactorQueryGateway.registerDispatchInterceptor(new LoggingReactorMessageDispatchInterceptor<>());
    }
}
