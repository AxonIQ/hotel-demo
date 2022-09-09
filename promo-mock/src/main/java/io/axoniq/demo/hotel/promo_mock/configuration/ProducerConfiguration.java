package io.axoniq.demo.hotel.promo_mock.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.axoniq.demo.hotel.promo.BookingDate;
import io.axoniq.demo.hotel.promo.PromoBookingAttempt;
import io.axoniq.demo.hotel.promo.RoomNumberRange;
import io.axoniq.demo.hotel.promo.TopicConstants;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.v1.CloudEventBuilder;
import io.cloudevents.kafka.CloudEventSerializer;
import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import io.axoniq.demo.hotel.promo_mock.state.PromoBookingIdsInTransit;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

@UtilityClass
public class ProducerConfiguration {

    private final List<RoomNumberRange> ROOM_NUMBER_RANGES = List.of(new RoomNumberRange(100, 104),
                                                                     new RoomNumberRange(105, 109),
                                                                     new RoomNumberRange(200, 204),
                                                                     new RoomNumberRange(205, 209));
    private final Random RANDOM = new Random();
    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final URI GIT_URI = URI.create("https://github.com/AxonIQ/hotel-demo");
    private final ZoneId AMSTERDAM_ZONE_ID = ZoneId.of("Europe/Amsterdam");

    public Properties getProperties() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CloudEventSerializer.class);
        return properties;
    }

    private CloudEvent getCloudEvent() throws JsonProcessingException {
        Instant startBooking = Instant.now().plus(RANDOM.nextInt(500) + 7L, ChronoUnit.DAYS);
        UUID promoBookingId = UUID.randomUUID();
        PromoBookingIdsInTransit.add(promoBookingId);
        PromoBookingAttempt promoBookingAttempt = new PromoBookingAttempt(
                ROOM_NUMBER_RANGES.get(RANDOM.nextInt(4)),
                new BookingDate(startBooking, AMSTERDAM_ZONE_ID),
                new BookingDate(startBooking.plus(2, ChronoUnit.DAYS), AMSTERDAM_ZONE_ID),
                promoBookingId
        );
        String data = OBJECT_MAPPER.writeValueAsString(promoBookingAttempt);
        return new CloudEventBuilder()
                .withId(UUID.randomUUID().toString())
                .withSource(GIT_URI)
                .withType(promoBookingAttempt.getClass().getCanonicalName())
                .withData(data.getBytes())
                .withExtension("traceid", UUID.randomUUID().toString())
                .withExtension("correlationid", UUID.randomUUID().toString())
                .build();
    }

    public ProducerRecord<String, CloudEvent> getProducerRecord() throws JsonProcessingException {
        CloudEvent cloudEvent = getCloudEvent();
        return new ProducerRecord<>(TopicConstants.PROMO_REQUESTS, cloudEvent.getId(), cloudEvent);
    }
}
