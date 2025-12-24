package com.recipeadjuster.analytics.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipeadjuster.analytics.model.entity.EventTracking;
import com.recipeadjuster.analytics.repository.EventTrackingRepository;
import com.recipeadjuster.shared.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventTrackingService {

    private final EventTrackingRepository eventTrackingRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void trackIngredientSubmission(IngredientSubmittedEvent event) {
        EventTracking tracking = EventTracking.builder()
            .eventId(event.getEventId())
            .eventType(event.getEventType())
            .timestamp(event.getTimestamp())
            .userId(event.getPayload().getUserId())
            .sessionId(event.getPayload().getSessionId())
            .metadata(serializeMetadata(event.getPayload()))
            .build();
        
        eventTrackingRepository.save(tracking);
        log.debug("Tracked ingredient submission for user: {}", event.getPayload().getUserId());
    }

    @Transactional
    public void trackRecipeMatch(RecipeMatchedEvent event) {
        EventTracking tracking = EventTracking.builder()
            .eventId(event.getEventId())
            .eventType(event.getEventType())
            .timestamp(event.getTimestamp())
            .userId(event.getPayload().getUserId())
            .sessionId(event.getPayload().getSessionId())
            .recipeId(event.getPayload().getRecipeId())
            .metadata(serializeMetadata(event.getPayload()))
            .build();
        
        eventTrackingRepository.save(tracking);
        log.debug("Tracked recipe match for recipe: {}", event.getPayload().getRecipeId());
    }

    @Transactional
    public void trackSubstitution(SubstitutionCompletedEvent event) {
        EventTracking tracking = EventTracking.builder()
            .eventId(event.getEventId())
            .eventType(event.getEventType())
            .timestamp(event.getTimestamp())
            .userId(event.getPayload().getUserId())
            .recipeId(event.getPayload().getRecipeId())
            .metadata(serializeMetadata(event.getPayload()))
            .build();
        
        eventTrackingRepository.save(tracking);
        log.debug("Tracked substitution for user: {}", event.getPayload().getUserId());
    }

    @Transactional
    public void trackUserPreferenceUpdate(UserPreferenceUpdatedEvent event) {
        EventTracking tracking = EventTracking.builder()
            .eventId(event.getEventId())
            .eventType(event.getEventType())
            .timestamp(event.getTimestamp())
            .userId(event.getPayload().getUserId())
            .metadata(serializeMetadata(event.getPayload()))
            .build();
        
        eventTrackingRepository.save(tracking);
        log.debug("Tracked preference update for user: {}", event.getPayload().getUserId());
    }

    private String serializeMetadata(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize metadata", e);
            return "{}";
        }
    }
}
