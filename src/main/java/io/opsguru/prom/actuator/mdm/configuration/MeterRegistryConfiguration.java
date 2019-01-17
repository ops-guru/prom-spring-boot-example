package io.opsguru.prom.actuator.mdm.configuration;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeterRegistryConfiguration {
    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
                // add some tags for microservice
                .commonTags("service", "mdm")
                .commonTags("environment", "test")
                .commonTags("region", "us-east-1");
    }
}
