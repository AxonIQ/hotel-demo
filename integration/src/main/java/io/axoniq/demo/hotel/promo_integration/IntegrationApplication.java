package io.axoniq.demo.hotel.promo_integration;

import io.cloudevents.CloudEvent;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.extensions.kafka.eventhandling.consumer.streamable.StreamableKafkaMessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IntegrationApplication {

    public static final String PROMO_PROCESSOR_NAME = "promo_processor";

    public static void main(String[] args) {
        SpringApplication.run(IntegrationApplication.class, args);
    }

    @Autowired
    public void registerProcessor(
            EventProcessingConfigurer processingConfigurer,
            StreamableKafkaMessageSource<String, CloudEvent> messageSource
    ) {
        processingConfigurer.registerPooledStreamingEventProcessor(PROMO_PROCESSOR_NAME, c -> messageSource);
    }
}
