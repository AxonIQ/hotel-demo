package io.axoniq.demo.hotel.booking.command.config;

import org.axonframework.axonserver.connector.TargetContextResolver;
import org.axonframework.axonserver.connector.event.axon.AxonServerEventStore;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventsourcing.MultiStreamableMessageSource;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("enterprise")
@Configuration
public class BookingCommandEnterpriseConfiguration {

    private static final String BOOKING = "booking";
    private static final String INVENTORY = "inventory";

    /**
     * Axon Server Enterprise Edition allows you to create multiple contexts that create logically separated message
     * buses within a single Axon Server cluster.
     * Now that we have our Booking and Inventory contexts created in Axon Server Enterprise we need to add
     * configuration logic to our Booking microservice to be able to send commands to other contexts (Inventory).
     */
    @Bean
    public TargetContextResolver<org.axonframework.messaging.Message<?>> targetContextResolver() {
        return message -> {
            if (message.getPayloadType().getPackage().getName().contains(".inventory.")) {
                return INVENTORY;
            } else {
                return BOOKING;
            }
        };
    }

    /**
     * AxonServerEventStore has a method 'createStreamableMessageSourceForContext(String)', which allows you to instantiate a StreamableMessageSource for a specific (bounded) context.
     * This, in conjunction with the Multi-Source Event Processing feature allows users to specify a TrackingEventProcessor that can ingest events from several contexts.
     */
    @Autowired
    public void configure(EventProcessingConfigurer configurer, EventStore eventStore) {
        if (eventStore instanceof AxonServerEventStore) {
            configurer.registerTrackingEventProcessor("BookingInventorySaga",
                                                      it -> MultiStreamableMessageSource.builder()
                                                                                        .addMessageSource(BOOKING, ((AxonServerEventStore)eventStore).createStreamableMessageSourceForContext(BOOKING))
                                                                                        .addMessageSource(INVENTORY, ((AxonServerEventStore)eventStore).createStreamableMessageSourceForContext(INVENTORY))
                                                                                        .longPollingSource(BOOKING)
                                                                                        .build());
        }
    }
}
