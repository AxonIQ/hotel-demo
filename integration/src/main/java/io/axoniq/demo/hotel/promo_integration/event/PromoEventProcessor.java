package io.axoniq.demo.hotel.promo_integration.event;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.Message;
import org.springframework.stereotype.Component;

import static io.axoniq.demo.hotel.promo_integration.IntegrationApplication.PROMO_PROCESSOR_NAME;

@Slf4j
@Component
@ProcessingGroup(PROMO_PROCESSOR_NAME)
public class PromoEventProcessor {

    @EventHandler
    public void on(Object event, Message<?> eventMessage) {
        log.info("Received event: '{}' with id: '{}' and metadata: '{}'",
                 event, eventMessage.getIdentifier(), eventMessage.getMetaData());
    }
}
