package com.recipeadjuster.shared.tracing;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TracingConfigTest {

    @Mock
    private Tracer tracer;

    @Mock
    private Span span;

    @Mock
    private TraceContext traceContext;

    private TracingConfig tracingConfig;

    @BeforeEach
    void setUp() {
        tracingConfig = new TracingConfig(tracer);
    }

    @Test
    void shouldPropagateTraceContextWhenSpanExists() {
        when(tracer.currentSpan()).thenReturn(span);
        when(span.context()).thenReturn(traceContext);
        when(traceContext.traceId()).thenReturn("trace-123");
        when(traceContext.spanId()).thenReturn("span-456");

        Message<String> message = tracingConfig.propagateTraceContext("test-payload");

        assertThat(message.getPayload()).isEqualTo("test-payload");
        assertThat(message.getHeaders().get("X-B3-TraceId")).isEqualTo("trace-123");
        assertThat(message.getHeaders().get("X-B3-SpanId")).isEqualTo("span-456");
        assertThat(message.getHeaders().get("X-B3-Sampled")).isEqualTo("1");
    }

    @Test
    void shouldCreateMessageWithoutTraceContextWhenNoSpan() {
        when(tracer.currentSpan()).thenReturn(null);

        Message<String> message = tracingConfig.propagateTraceContext("test-payload");

        assertThat(message.getPayload()).isEqualTo("test-payload");
        assertThat(message.getHeaders().get("X-B3-TraceId")).isNull();
        assertThat(message.getHeaders().get("X-B3-SpanId")).isNull();
    }
}
