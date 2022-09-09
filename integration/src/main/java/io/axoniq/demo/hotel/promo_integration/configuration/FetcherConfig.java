package io.axoniq.demo.hotel.promo_integration.configuration;

import io.axoniq.demo.hotel.promo.TopicConstants;
import io.cloudevents.CloudEvent;
import org.axonframework.extensions.kafka.eventhandling.KafkaMessageConverter;
import org.axonframework.extensions.kafka.eventhandling.consumer.ConsumerFactory;
import org.axonframework.extensions.kafka.eventhandling.consumer.Fetcher;
import org.axonframework.extensions.kafka.eventhandling.consumer.streamable.KafkaEventMessage;
import org.axonframework.extensions.kafka.eventhandling.consumer.streamable.StreamableKafkaMessageSource;
import org.axonframework.serialization.Serializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Collections;

@Profile("after-task-1")
@Configuration
public class FetcherConfig {

    @Bean
    public StreamableKafkaMessageSource<String, CloudEvent> streamableKafkaMessageSource(
            @Qualifier("eventSerializer") Serializer eventSerializer,
            ConsumerFactory<String, CloudEvent> kafkaConsumerFactory,
            Fetcher<String, CloudEvent, KafkaEventMessage> kafkaFetcher,
            KafkaMessageConverter<String, CloudEvent> kafkaMessageConverter) {
        StreamableKafkaMessageSource.Builder<String, CloudEvent> builder = StreamableKafkaMessageSource.builder();
        return builder
                .topics(Collections.singletonList(TopicConstants.PROMO_REQUESTS))
                .serializer(eventSerializer)
                .consumerFactory(kafkaConsumerFactory)
                .fetcher(kafkaFetcher)
                .messageConverter(kafkaMessageConverter)
                .build();
    }
}
