package com.recipeadjuster.analytics.consumer;

import com.recipeadjuster.analytics.service.EventTrackingService;
import com.recipeadjuster.shared.event.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsEventConsumerTest {

    @Mock
    private EventTrackingService eventTrackingService;

    @InjectMocks
    private AnalyticsEventConsumer consumer;

    @Test
    void shouldConsumeIngredientSubmittedEvent() {
        IngredientSubmittedEvent event = IngredientSubmittedEvent.builder()
            .eventId("event-123")
            .eventType("ingredient.submitted")
            .timestamp(Instant.now())
            .source("test")
            .correlationId("corr-123")
            .payload(IngredientSubmittedEvent.Payload.builder()
                .userId("user-1")
                .sessionId("session-1")
                .ingredients(List.of("chicken", "garlic"))
                .build())
            .build();

        consumer.consumeIngredientSubmitted(event);

        verify(eventTrackingService, times(1)).trackIngredientSubmission(event);
    }

    @Test
    void shouldConsumeRecipeMatchedEvent() {
        RecipeMatchedEvent event = RecipeMatchedEvent.builder()
            .eventId("event-456")
            .eventType("recipe.matched")
            .timestamp(Instant.now())
            .source("test")
            .correlationId("corr-456")
            .payload(RecipeMatchedEvent.Payload.builder()
                .userId("user-1")
                .sessionId("session-1")
                .recipeId("recipe-1")
                .recipeName("Chicken Pasta")
                .matchPercentage(85)
                .build())
            .build();

        consumer.consumeRecipeMatched(event);

        verify(eventTrackingService, times(1)).trackRecipeMatch(event);
    }

    @Test
    void shouldConsumeSubstitutionCompletedEvent() {
        SubstitutionCompletedEvent event = SubstitutionCompletedEvent.builder()
            .eventId("event-789")
            .eventType("substitution.completed")
            .timestamp(Instant.now())
            .source("test")
            .correlationId("corr-789")
            .payload(SubstitutionCompletedEvent.Payload.builder()
                .userId("user-1")
                .recipeId("recipe-1")
                .originalIngredient("butter")
                .suggestedSubstitutions(List.of("margarine", "oil"))
                .substitutionCount(2)
                .build())
            .build();

        consumer.consumeSubstitutionCompleted(event);

        verify(eventTrackingService, times(1)).trackSubstitution(event);
    }

    @Test
    void shouldConsumeUserPreferenceUpdatedEvent() {
        UserPreferenceUpdatedEvent event = UserPreferenceUpdatedEvent.builder()
            .eventId("event-101")
            .eventType("user.preference.updated")
            .timestamp(Instant.now())
            .source("test")
            .correlationId("corr-101")
            .payload(UserPreferenceUpdatedEvent.Payload.builder()
                .userId("user-1")
                .dietaryRestrictions(List.of("vegetarian"))
                .cuisinePreference("Italian")
                .maxCookTime(30)
                .build())
            .build();

        consumer.consumeUserPreferenceUpdated(event);

        verify(eventTrackingService, times(1)).trackUserPreferenceUpdate(event);
    }
}
