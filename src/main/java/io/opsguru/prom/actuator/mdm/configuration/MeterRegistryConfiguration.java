package io.opsguru.prom.actuator.mdm.configuration;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.prometheus.PrometheusNamingConvention;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class MeterRegistryConfiguration {
    private static final Duration HISTOGRAM_EXPIRY = Duration.ofMinutes(10);

    private static final Duration STEP = Duration.ofSeconds(5);

    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        Logger logger = LoggerFactory.getLogger(MeterRegistry.class);
        return registry -> registry.config()
                // Set naming configuration (optional)
                .namingConvention(new PrometheusNamingConvention())
                // Simple example how to intercept added meter
                .onMeterAdded(meter -> logger.info("New meter added with ID {}", meter.getId().getName()))
                // Skipping not important endpoints will limit unwanted data
                .meterFilter(MeterFilter.deny(id -> { // (4)
                    String uri = id.getTag("uri");
                    return uri != null && uri.startsWith("/swagger");
                }))
                .meterFilter(new MeterFilter() {
                    @Override
                    public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
                        return config.merge(DistributionStatisticConfig.builder()
                                .percentilesHistogram(true)
                                // A list of percentiles you would like to track
                                .percentiles(0.9, 0.95, 0.99, 0.999)
                                /*
                                    Histograms are calculated for some defined time window where more recent values
                                    have bigger impact on final value. The bigger time window you choose,
                                    the more accurate statistics are, but the less sudden will be changes
                                    of percentile value in case of very big or very small response time.
                                    It is also very important to increase buffer length as you increase expiry time.
                                 */
                                .expiry(HISTOGRAM_EXPIRY)
                                .bufferLength((int) (HISTOGRAM_EXPIRY.toMillis() / STEP.toMillis()))
                                .build());
                    }
                })
                // Tags that will be added to every metric. As you can see it’s very handy for identifying hosts in a cluster
                .commonTags("service", "mdm")
                .commonTags("environment", "test")
                .commonTags("region", "us-east-1");
    }
}
