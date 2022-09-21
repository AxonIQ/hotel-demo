package io.axoniq.demo.hotel.promo_integration.configuration;

import io.axoniq.demo.hotel.promo.PromoBookingFailed;
import io.axoniq.demo.hotel.promo.PromoBookingSucceeded;
import io.axoniq.demo.hotel.promo.TopicConstants;
import io.cloudevents.CloudEvent;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.extensions.kafka.eventhandling.KafkaMessageConverter;
import org.axonframework.extensions.kafka.eventhandling.producer.KafkaPublisher;
import org.axonframework.extensions.kafka.eventhandling.producer.ProducerFactory;
import org.axonframework.serialization.Serializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class PublisherConfig {

    @Bean(destroyMethod = "shutDown")
    public KafkaPublisher<String, CloudEvent> kafkaPublisher(
            @Qualifier("eventSerializer") Serializer eventSerializer,
            ProducerFactory<String, CloudEvent> axonKafkaProducerFactory,
            KafkaMessageConverter<String, CloudEvent> kafkaMessageConverter,
            org.axonframework.config.Configuration configuration) {
        return KafkaPublisher.<String, CloudEvent>builder()
                             .serializer(eventSerializer)
                             .producerFactory(axonKafkaProducerFactory)
                             .messageConverter(kafkaMessageConverter)
                             .messageMonitor(configuration.messageMonitor(KafkaPublisher.class, "kafkaPublisher"))
                             .topicResolver(this::topicResolver)
                             .build();
    }

    private Optional<String> topicResolver(EventMessage<?> message){
        Class<?>  type = message.getPayloadType();
        if (type.isAssignableFrom(PromoBookingSucceeded.class)
                || type.isAssignableFrom(PromoBookingFailed.class)) {
            return Optional.of(TopicConstants.PROMO_REPLIES);
        } else {
            return Optional.empty();
        }
    }
}
