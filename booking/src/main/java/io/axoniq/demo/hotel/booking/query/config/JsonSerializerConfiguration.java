package io.axoniq.demo.hotel.booking.query.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonSerializerConfiguration {

    @Bean("objectMapper")
    public ObjectMapper jsonObjectMapper() {
        return new ObjectMapper()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setDefaultTyping(new ContainerAwareTypeResolver())
                .registerModule(new Jdk8Module())
                .registerModule(new KotlinModule())
                .registerModule(new JavaTimeModule());
    }

    @Bean("messageSerializer")
    public Serializer messageSerializer(ObjectMapper objectMapper) {
        return JacksonSerializer
                .builder()
                .objectMapper(objectMapper)
                .lenientDeserialization()
                .build();
    }

    /**
     * Class used to exclude containers type from serialized data object.
     */
    private static class ContainerAwareTypeResolver extends ObjectMapper.DefaultTypeResolverBuilder {

        public ContainerAwareTypeResolver() {
            super(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE);
        }

        {
            init(JsonTypeInfo.Id.CLASS, null);
            inclusion(JsonTypeInfo.As.PROPERTY);
            typeProperty("@type");
        }

        @Override
        public boolean useForType(JavaType t) {
            return !t.isContainerType() && super.useForType(t);
        }
    }
}
