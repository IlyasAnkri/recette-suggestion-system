package com.recipeadjuster.shared.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MetricsServiceTest {

    private MeterRegistry meterRegistry;
    private MetricsService metricsService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        metricsService = new MetricsService(meterRegistry);
    }

    @Test
    void shouldIncrementSearchCounter() {
        metricsService.incrementSearchCounter();
        metricsService.incrementSearchCounter();

        Counter counter = meterRegistry.find("searches.total").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(2.0);
    }

    @Test
    void shouldIncrementSubstitutionCounter() {
        metricsService.incrementSubstitutionCounter();

        Counter counter = meterRegistry.find("substitutions.total").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    void shouldIncrementRecipeViewCounter() {
        metricsService.incrementRecipeViewCounter("recipe-123");
        metricsService.incrementRecipeViewCounter("recipe-123");

        Counter counter = meterRegistry.find("recipe.views.total")
            .tag("recipe_id", "recipe-123")
            .counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(2.0);
    }

    @Test
    void shouldRecordSearchDuration() {
        metricsService.recordSearchDuration(150);

        Timer timer = meterRegistry.find("search.duration").timer();
        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(1);
    }

    @Test
    void shouldIncrementKafkaEventPublished() {
        metricsService.incrementKafkaEventPublished("ingredient.submitted");

        Counter counter = meterRegistry.find("kafka.events.published")
            .tag("event_type", "ingredient.submitted")
            .counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    void shouldIncrementKafkaEventConsumed() {
        metricsService.incrementKafkaEventConsumed("recipe.matched");

        Counter counter = meterRegistry.find("kafka.events.consumed")
            .tag("event_type", "recipe.matched")
            .counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }
}
