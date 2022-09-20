
package  io.axoniq.demo.hotel.booking.management;

import org.axonframework.extensions.multitenancy.components.TenantConnectPredicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


public class DevOpsController {
    @PostMapping(path = "/reset")
    public void resetAll() {
        configuration.eventProcessingConfiguration()
                     .eventProcessors().values().stream().filter(instance -> instance instanceof MultiTenantEventProcessor)
                     .findFirst()
                     .ifPresent(mtEventProcessor -> {
                         ((MultiTenantEventProcessor) mtEventProcessor).tenantSegments().values().stream()
                                                                       .filter(i -> i instanceof StreamingEventProcessor)
                                                                       .map(i-> (StreamingEventProcessor) i)
                                                                       .forEach(eventProcessor -> {
                                                                           if (eventProcessor.supportsReset()) {
                                                                               eventProcessor.shutDown();
                                                                               eventProcessor.resetTokens(StreamableMessageSource::createTailToken);
                                                                               eventProcessor.start();
                                                                           }
                                                                       });
                     });
    }
}
