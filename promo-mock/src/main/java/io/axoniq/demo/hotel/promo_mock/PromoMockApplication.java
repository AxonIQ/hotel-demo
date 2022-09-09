package io.axoniq.demo.hotel.promo_mock;

import io.axoniq.demo.hotel.promo.TopicConstants;
import io.axoniq.demo.hotel.promo_mock.configuration.ProducerConfiguration;
import io.cloudevents.CloudEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import io.axoniq.demo.hotel.promo_mock.configuration.ConsumerConfiguration;
import io.axoniq.demo.hotel.promo_mock.state.PromoBookingIdsInTransit;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class PromoMockApplication {

    public static void main(String[] args) {
        AtomicInteger counter = new AtomicInteger(0);
        ExecutorService consumerExecutor = Executors.newSingleThreadExecutor();
        consumerExecutor.submit(() -> consume(counter));
        try (KafkaProducer<String, CloudEvent> producer = new KafkaProducer<>(ProducerConfiguration.getProperties())) {
            while (counter.get() < 180) {
                producer.send(ProducerConfiguration.getProducerRecord());
                int i = counter.incrementAndGet();
                log.info("Just send event {} of 180", i);
                Thread.sleep(10_000L);
            }
        } catch (Exception e) {
            log.warn("Encountered exception, will exit", e);
        } finally {
            consumerExecutor.shutdownNow();
        }
    }

    private static void consume(AtomicInteger counter) {
        AtomicLong pollCounter = new AtomicLong(0);
        try (KafkaConsumer<String, CloudEvent> consumer = new KafkaConsumer<>(ConsumerConfiguration.getProperties())) {
            consumer.subscribe(Collections.singletonList(TopicConstants.PROMO_REPLIES));
            while (counter.get() < 180 || PromoBookingIdsInTransit.size() > 0) {
                ConsumerRecords<String, CloudEvent> records = consumer.poll(Duration.ofSeconds(1L));
                ConsumerConfiguration.process(records);
                long polled = pollCounter.incrementAndGet();
                if (polled % 100 == 0) {
                    log.info("Polled {} times totally, expecting {} replies.", polled, PromoBookingIdsInTransit.size());
                }
            }
        } catch (Exception e) {
            log.warn("Encountered exception, will stop consuming", e);
        }
    }
}
