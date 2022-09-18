package io.axoniq.demo.hotel.booking.command.config;

import org.axonframework.extensions.multitenancy.components.TenantConnectPredicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MultiTenancyConfig {

    @Bean
    public TenantConnectPredicate tenantFilter() {
        return context -> true;
    }
}
