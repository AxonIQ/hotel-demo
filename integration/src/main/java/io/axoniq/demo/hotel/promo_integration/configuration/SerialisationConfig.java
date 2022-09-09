package io.axoniq.demo.hotel.promo_integration.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.kafka.common.TopicPartition;
import org.axonframework.extensions.kafka.eventhandling.consumer.streamable.TopicPartitionDeserializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SerialisationConfig {

    @Bean
    @Primary
    public JacksonSerializer axonJacksonSerializer(ObjectMapper objectMapper) {
        objectMapper.registerModule(new TopicPartitionJacksonModule());
        return JacksonSerializer.builder()
                                .objectMapper(objectMapper)
                                .defaultTyping()
                                .build();
    }

    private static class TopicPartitionJacksonModule extends SimpleModule {

        public TopicPartitionJacksonModule() {
            addKeyDeserializer(
                    TopicPartition.class,
                    new TopicPartitionDeserializer());
        }
    }
}
