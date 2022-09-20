package io.axoniq.demo.hotel.booking.command.config;

import org.axonframework.extensions.multitenancy.components.TenantConnectPredicate;
import org.axonframework.extensions.multitenancy.components.TenantDescriptor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class MultiTenancyConfig {

    @Bean
    public TenantConnectPredicate tenantFilter() {
        return context -> true;
    }

}
