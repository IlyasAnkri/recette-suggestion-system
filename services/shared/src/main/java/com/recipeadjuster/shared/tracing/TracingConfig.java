package com.recipeadjuster.shared.tracing;

import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@Configuration
@RequiredArgsConstructor
public class TracingConfig {

    private final Tracer tracer;

    public <T> Message<T> propagateTraceContext(T payload) {
        var currentSpan = tracer.currentSpan();
        if (currentSpan == null) {
            return MessageBuilder.withPayload(payload).build();
        }

        var traceContext = currentSpan.context();
        return MessageBuilder.withPayload(payload)
            .setHeader("X-B3-TraceId", traceContext.traceId())
            .setHeader("X-B3-SpanId", traceContext.spanId())
            .setHeader("X-B3-Sampled", "1")
            .build();
    }

    public void extractTraceContext(Message<?> message) {
        String traceId = message.getHeaders().get("X-B3-TraceId", String.class);
        String spanId = message.getHeaders().get("X-B3-SpanId", String.class);
        
        if (traceId != null && spanId != null) {
            // Trace context will be automatically propagated by Micrometer
        }
    }
}
