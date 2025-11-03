package com.deliverytech.delivery_api.config;

import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;

@Configuration 
public class MicrometerConfig { 
 
    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() { 
        return registry -> { 
            registry.config() 
                .commonTags("application", "delivery-api") 
                .commonTags("environment", "development") 
                .commonTags("version", "1.0.0") 
                .meterFilter(MeterFilter.deny(id -> { 
                    String uri = id.getTag("uri"); 
                    return uri != null && uri.startsWith("/actuator"); 
                })); 
        }; 
    } 
} 
