/*
 * Copyright (c) 2020-2020. AxonIQ
 *
 * Licensed under the Apache License, Version 2.0 (the &quot;License&quot;)
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.axoniq.demo.hotel.booking.command.config;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.common.caching.Cache;
import org.axonframework.common.caching.WeakReferenceCache;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
import org.axonframework.messaging.interceptors.LoggingInterceptor;
import org.axonframework.spring.eventsourcing.SpringAggregateSnapshotterFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

@Configuration
@ConfigurationPropertiesScan
public class BookingCommandConfiguration {

    /************************************************/
    /* Register interceptors on the bus */
    /************************************************/

    @Autowired
    public void registerCommandInterceptorsOnBus(CommandBus commandBus) {
        commandBus.registerHandlerInterceptor(new LoggingInterceptor<>());
    }

    @Autowired
    public void registerEventInterceptors(EventBus eventBus) {
        eventBus.registerDispatchInterceptor(new LoggingInterceptor<>());
    }

    @Autowired
    public void configure(EventProcessingConfigurer config) {
        config.registerDefaultHandlerInterceptor((t, u) -> new LoggingInterceptor<>(u));
    }

    /***************************************/
    /*  Aggregate cache configuration   */
    /***************************************/

    @Bean("cache")
    public Cache cache() {
        return new WeakReferenceCache();
    }

    /***************************************/
    /*  Aggregate snapshot configuration   */
    /***************************************/

    @Bean
    public SpringAggregateSnapshotterFactoryBean snapshotter() {
        var springAggregateSnapshotterFactoryBean = new SpringAggregateSnapshotterFactoryBean();
        //Setting async executors
        springAggregateSnapshotterFactoryBean.setExecutor(Executors.newSingleThreadExecutor());
        return springAggregateSnapshotterFactoryBean;
    }

    @Bean("roomSnapshotTriggerDefinition")
    EventCountSnapshotTriggerDefinition roomSnapshotTriggerDefinition(Snapshotter snapshotter, BookingCommandProperties bookingCommandProperties) {
        return new EventCountSnapshotTriggerDefinition(snapshotter, bookingCommandProperties.getSnapshotTriggerThresholdRoom());
    }

    @Bean("accountSnapshotTriggerDefinition")
    EventCountSnapshotTriggerDefinition accountSnapshotTriggerDefinition(Snapshotter snapshotter, BookingCommandProperties bookingCommandProperties) {
        return new EventCountSnapshotTriggerDefinition(snapshotter, bookingCommandProperties.getSnapshotTriggerThresholdAccount());
    }

    @Bean("paymentSnapshotTriggerDefinition")
    EventCountSnapshotTriggerDefinition paymentSnapshotTriggerDefinition(Snapshotter snapshotter, BookingCommandProperties bookingCommandProperties) {
        return new EventCountSnapshotTriggerDefinition(snapshotter, bookingCommandProperties.getSnapshotTriggerThresholdPayment());
    }
}
