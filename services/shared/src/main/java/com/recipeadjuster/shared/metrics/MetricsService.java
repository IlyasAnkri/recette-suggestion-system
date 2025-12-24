package com.recipeadjuster.shared.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MetricsService {

    private final MeterRegistry meterRegistry;

    public void incrementSearchCounter() {
        Counter.builder("searches.total")
            .description("Total number of ingredient searches")
            .register(meterRegistry)
            .increment();
    }

    public void incrementSubstitutionCounter() {
        Counter.builder("substitutions.total")
            .description("Total number of substitution requests")
            .register(meterRegistry)
            .increment();
    }

    public void incrementRecipeViewCounter(String recipeId) {
        Counter.builder("recipe.views.total")
            .description("Total number of recipe views")
            .tag("recipe_id", recipeId)
            .register(meterRegistry)
            .increment();
    }

    public void recordSearchDuration(long durationMs) {
        Timer.builder("search.duration")
            .description("Duration of search operations")
            .register(meterRegistry)
            .record(durationMs, TimeUnit.MILLISECONDS);
    }

    public void recordSubstitutionDuration(long durationMs) {
        Timer.builder("substitution.duration")
            .description("Duration of substitution operations")
            .register(meterRegistry)
            .record(durationMs, TimeUnit.MILLISECONDS);
    }

    public void incrementKafkaEventPublished(String eventType) {
        Counter.builder("kafka.events.published")
            .description("Total Kafka events published")
            .tag("event_type", eventType)
            .register(meterRegistry)
            .increment();
    }

    public void incrementKafkaEventConsumed(String eventType) {
        Counter.builder("kafka.events.consumed")
            .description("Total Kafka events consumed")
            .tag("event_type", eventType)
            .register(meterRegistry)
            .increment();
    }
}
