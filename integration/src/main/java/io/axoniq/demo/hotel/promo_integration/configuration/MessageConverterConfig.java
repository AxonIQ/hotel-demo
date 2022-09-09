package io.axoniq.demo.hotel.promo_integration.configuration;

import io.cloudevents.CloudEvent;
import org.axonframework.extensions.kafka.eventhandling.KafkaMessageConverter;
import org.axonframework.extensions.kafka.eventhandling.cloudevent.CloudEventKafkaMessageConverter;
import org.axonframework.serialization.Serializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageConverterConfig {

    @Bean
    public KafkaMessageConverter<String, CloudEvent> kafkaMessageConverter(
            @Qualifier("eventSerializer") Serializer eventSerializer
    ) {
        return CloudEventKafkaMessageConverter
                .builder()
                .serializer(eventSerializer)
                .addMetadataMapper("uber-trace-id", "ubertraceid")
                .build();
    }
}
