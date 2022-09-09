package io.axoniq.demo.hotel.promo_mock.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.axoniq.demo.hotel.promo.PromoBookingFailed;
import io.axoniq.demo.hotel.promo.PromoBookingSucceeded;
import io.axoniq.demo.hotel.promo_mock.state.PromoBookingIdsInTransit;
import io.cloudevents.CloudEvent;
import io.cloudevents.kafka.CloudEventDeserializer;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

@Slf4j
@UtilityClass
public class ConsumerConfiguration {

    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public Properties getProperties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "axoniq-hotel-promo-group");
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CloudEventDeserializer.class);
        return properties;
    }

    public void process(ConsumerRecords<String, CloudEvent> records) {
        records.forEach(r -> {
            CloudEvent event = r.value();
            processEvent(event);
        });
    }

    private void processEvent(CloudEvent event) {
        try {
            JsonNode result = OBJECT_MAPPER.readTree(
                    Objects.requireNonNull(event.getData()).toBytes());
            UUID promoBookingId = UUID.fromString(result.get("promoBookingId").asText());
            handleResult(promoBookingId, result);
        } catch (Exception e) {
            log.warn("Encountered exception handling {}", event, e);
        }
    }

    private void handleResult(UUID promoBookingId, Object result) {
        if (PromoBookingIdsInTransit.remove(promoBookingId)) {
            log.info("Received {}, {} waiting reply", result, PromoBookingIdsInTransit.size());
        } else {
            log.warn("promoBookingId could not be removed from list, from event: {}", result);
        }
    }
}
