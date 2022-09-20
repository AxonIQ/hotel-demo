package io.axoniq.demo.hotel.booking.command.config;

import org.axonframework.extensions.multitenancy.components.TenantConnectPredicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MultiTenancyConfig {

    @Bean
    public TenantConnectPredicate tenantFilter() {
        return context -> context.tenantId().startsWith("booking-");
    }

    @Bean
    public Function<TenantDescriptor, DataSourceProperties> tenantDataSourceResolver() {
        return tenant -> {
            DataSourceProperties properties = new DataSourceProperties();
            properties.setUrl("jdbc:h2:mem:"+tenant.tenantId());
            properties.setDriverClassName("org.h2.Driver");
            properties.setUsername("sa");
            return properties;
        };
    }
}
