package io.axoniq.demo.hotel.booking.serialisation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.axonframework.serialization.json.JacksonSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SerialisationConfig {

    @Bean
    @Primary
    public JacksonSerializer axonJacksonSerializer(ObjectMapper objectMapper) {
        return JacksonSerializer.builder()
                                .objectMapper(objectMapper)
                                .defaultTyping()
                                .build();
    }
}
