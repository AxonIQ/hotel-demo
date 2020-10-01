//package io.axoniq.demo.hotel.booking.config;
//
//import io.micrometer.core.instrument.MeterRegistry;
//import io.micrometer.core.instrument.Tag;
//import io.micrometer.core.instrument.Tags;
//import org.axonframework.commandhandling.CommandBus;
//import org.axonframework.config.Configurer;
//import org.axonframework.config.ConfigurerModule;
//import org.axonframework.config.MessageMonitorFactory;
//import org.axonframework.eventhandling.TrackingEventProcessor;
//import org.axonframework.eventsourcing.eventstore.EventStore;
//import org.axonframework.micrometer.CapacityMonitor;
//import org.axonframework.micrometer.MessageCountingMonitor;
//import org.axonframework.micrometer.MessageTimerMonitor;
//import org.axonframework.micrometer.TagsUtil;
//import org.axonframework.monitoring.MultiMessageMonitor;
//import org.axonframework.queryhandling.QueryBus;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.stream.Collectors;
//
//@Configuration
//public class MetricsConfig {
//
//    @Bean
//    public ConfigurerModule metricConfigurer(MeterRegistry meterRegistry) {
//        return configurer -> {
//            instrumentEventStore(meterRegistry, configurer);
//            instrumentEventProcessors(meterRegistry, configurer);
//            instrumentCommandBus(meterRegistry, configurer);
//            instrumentQueryBus(meterRegistry, configurer);
//        };
//    }
//
//    // A custom/specific implementation of the micrometer metrics for the EventStore component.
//    private void instrumentEventStore(MeterRegistry meterRegistry, Configurer configurer) {
//        MessageMonitorFactory messageMonitorFactory = (configuration, componentType, componentName) -> {
//
//            // Naming the Counter monitor/meter with the name of the component (eventStore)
//            // Registering the Counter with custom tags: payloadType, messageId and the map off all metadata elements.
//            MessageCountingMonitor messageCounter = MessageCountingMonitor.buildMonitor(componentName,
//                                                                                        meterRegistry,
//                                                                                        message -> Tags
//                                                                                                .of(TagsUtil.PAYLOAD_TYPE_TAG,
//                                                                                                    message.getPayloadType()
//                                                                                                           .getSimpleName(),
//
//                                                                                                    "messageId",
//                                                                                                    message.getIdentifier())
//                                                                                                .and(message.getMetaData()
//                                                                                                            .entrySet()
//                                                                                                            .stream()
//                                                                                                            .map(s -> Tag
//                                                                                                                    .of(s.getKey(),
//                                                                                                                        s.getValue()
//                                                                                                                         .toString()))
//                                                                                                            .collect(
//                                                                                                                    Collectors
//                                                                                                                            .toList())));
//            // Naming the Timer monitor/meter with the name of the component (eventStore)
//            // Registering the Timer with custom tags: payloadType.
//            MessageTimerMonitor messageTimer = MessageTimerMonitor.buildMonitor(componentName,
//                                                                                meterRegistry,
//                                                                                message -> Tags
//                                                                                        .of(TagsUtil.PAYLOAD_TYPE_TAG,
//                                                                                            message.getPayloadType()
//                                                                                                   .getSimpleName()));
//            return new MultiMessageMonitor<>(messageCounter, messageTimer);
//        };
//        configurer.configureMessageMonitor(EventStore.class, messageMonitorFactory);
//    }
//
//    // A custom/specific implementation of the micrometer metrics for the (Tracking) Event Processor components.
//    private void instrumentEventProcessors(MeterRegistry meterRegistry, Configurer configurer) {
//        MessageMonitorFactory messageMonitorFactory = (configuration, componentType, componentName) -> {
//
//            // Naming the Counter monitor/meter with the fixed name `eventProcessor`. `Component name` is a Micrometer tag in this case.
//            // Registering the Counter with custom tags: payloadType and processorName.
//            MessageCountingMonitor messageCounter = MessageCountingMonitor.buildMonitor("eventProcessor",
//                                                                                        meterRegistry,
//                                                                                        message -> Tags
//                                                                                                .of(TagsUtil.PAYLOAD_TYPE_TAG,
//                                                                                                    message.getPayloadType()
//                                                                                                           .getSimpleName(),
//                                                                                                    TagsUtil.PROCESSOR_NAME_TAG,
//                                                                                                    componentName));
//            // Naming the Timer monitor/meter with the fixed name `eventProcessor`. `Component name` is a Micrometer tag in this case.
//            // Registering the Timer with custom tags: payloadType and processorName.
//            MessageTimerMonitor messageTimer = MessageTimerMonitor.buildMonitor("eventProcessor",
//                                                                                meterRegistry,
//                                                                                message -> Tags
//                                                                                        .of(TagsUtil.PAYLOAD_TYPE_TAG,
//                                                                                            message.getPayloadType()
//                                                                                                   .getSimpleName(),
//                                                                                            TagsUtil.PROCESSOR_NAME_TAG,
//                                                                                            componentName));
//            // Naming the Capacity/Gauge monitor/meter with the fixed name `eventProcessor`. `Component name` is a Micrometer tag in this case.
//            // Registering the Capacity/Gauge with custom tags: payloadType and processorName.
//            CapacityMonitor capacityMonitor1Minute = CapacityMonitor.buildMonitor("eventProcessor",
//                                                                                  meterRegistry,
//                                                                                  message -> Tags
//                                                                                          .of(TagsUtil.PAYLOAD_TYPE_TAG,
//                                                                                              message.getPayloadType()
//                                                                                                     .getSimpleName(),
//                                                                                              TagsUtil.PROCESSOR_NAME_TAG,
//                                                                                              componentName));
//
//            return new MultiMessageMonitor<>(messageCounter, messageTimer, capacityMonitor1Minute);
//        };
//        configurer.configureMessageMonitor(TrackingEventProcessor.class, messageMonitorFactory);
//    }
//
//    private void instrumentCommandBus(MeterRegistry meterRegistry, Configurer configurer) {
//        MessageMonitorFactory messageMonitorFactory = (configuration, componentType, componentName) -> {
//
//            MessageCountingMonitor messageCounter = MessageCountingMonitor.buildMonitor(componentName,
//                                                                                        meterRegistry,
//                                                                                        message -> Tags
//                                                                                                .of(TagsUtil.PAYLOAD_TYPE_TAG,
//                                                                                                    message.getPayloadType()
//                                                                                                           .getSimpleName(),
//
//                                                                                                    "messageId",
//                                                                                                    message.getIdentifier()));
//            MessageTimerMonitor messageTimer = MessageTimerMonitor.buildMonitor(componentName,
//                                                                                meterRegistry,
//                                                                                message -> Tags
//                                                                                        .of(TagsUtil.PAYLOAD_TYPE_TAG,
//                                                                                            message.getPayloadType()
//                                                                                                   .getSimpleName()));
//
//            CapacityMonitor capacityMonitor1Minute = CapacityMonitor.buildMonitor(componentName,
//                                                                                  meterRegistry,
//                                                                                  message -> Tags
//                                                                                          .of(TagsUtil.PAYLOAD_TYPE_TAG,
//                                                                                              message.getPayloadType()
//                                                                                                     .getSimpleName()));
//
//            return new MultiMessageMonitor<>(messageCounter, messageTimer, capacityMonitor1Minute);
//        };
//        configurer.configureMessageMonitor(CommandBus.class, messageMonitorFactory);
//    }
//
//    private void instrumentQueryBus(MeterRegistry meterRegistry, Configurer configurer) {
//        MessageMonitorFactory messageMonitorFactory = (configuration, componentType, componentName) -> {
//            MessageCountingMonitor messageCounter = MessageCountingMonitor.buildMonitor(componentName,
//                                                                                        meterRegistry,
//                                                                                        message -> Tags
//                                                                                                .of(TagsUtil.PAYLOAD_TYPE_TAG,
//                                                                                                    message.getPayloadType()
//                                                                                                           .getSimpleName(),
//
//                                                                                                    "messageId",
//                                                                                                    message.getIdentifier()));
//            MessageTimerMonitor messageTimer = MessageTimerMonitor.buildMonitor(componentName,
//                                                                                meterRegistry,
//                                                                                message -> Tags
//                                                                                        .of(TagsUtil.PAYLOAD_TYPE_TAG,
//                                                                                            message.getPayloadType()
//                                                                                                   .getSimpleName()));
//
//            CapacityMonitor capacityMonitor1Minute = CapacityMonitor.buildMonitor(componentName,
//                                                                                  meterRegistry,
//                                                                                  message -> Tags
//                                                                                          .of(TagsUtil.PAYLOAD_TYPE_TAG,
//                                                                                              message.getPayloadType()
//                                                                                                     .getSimpleName()));
//
//
//            return new MultiMessageMonitor<>(messageCounter, messageTimer, capacityMonitor1Minute);
//        };
//        configurer.configureMessageMonitor(QueryBus.class, messageMonitorFactory);
//    }
//}
