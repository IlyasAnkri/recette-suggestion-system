package com.recipeadjuster.analytics.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "event_tracking", indexes = {
    @Index(name = "idx_event_type", columnList = "eventType"),
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_timestamp", columnList = "timestamp")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private Instant timestamp;

    @Column
    private String userId;

    @Column
    private String sessionId;

    @Column
    private String recipeId;

    @Column(length = 2000)
    private String metadata;
}
