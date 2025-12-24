package com.recipeadjuster.shared.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class MetricsConfig {

    @Value("${spring.application.name}")
    private String serviceName;

    @Value("${ENVIRONMENT:dev}")
    private String environment;

    @Bean
    public MeterFilter commonTags() {
        return MeterFilter.commonTags(
            Arrays.asList(
                Tag.of("service", serviceName),
                Tag.of("environment", environment)
            )
        );
    }
}
