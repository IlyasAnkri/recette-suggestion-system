package com.recipeadjuster.analytics.consumer;

import com.recipeadjuster.analytics.service.EventTrackingService;
import com.recipeadjuster.shared.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsEventConsumer {

    private final EventTrackingService eventTrackingService;

    @KafkaListener(topics = "ingredient.submitted", groupId = "${spring.application.name}")
    public void consumeIngredientSubmitted(IngredientSubmittedEvent event) {
        log.info("Consumed ingredient.submitted event: {}", event.getEventId());
        eventTrackingService.trackIngredientSubmission(event);
    }

    @KafkaListener(topics = "recipe.matched", groupId = "${spring.application.name}")
    public void consumeRecipeMatched(RecipeMatchedEvent event) {
        log.info("Consumed recipe.matched event: {}", event.getEventId());
        eventTrackingService.trackRecipeMatch(event);
    }

    @KafkaListener(topics = "substitution.completed", groupId = "${spring.application.name}")
    public void consumeSubstitutionCompleted(SubstitutionCompletedEvent event) {
        log.info("Consumed substitution.completed event: {}", event.getEventId());
        eventTrackingService.trackSubstitution(event);
    }

    @KafkaListener(topics = "user.preference.updated", groupId = "${spring.application.name}")
    public void consumeUserPreferenceUpdated(UserPreferenceUpdatedEvent event) {
        log.info("Consumed user.preference.updated event: {}", event.getEventId());
        eventTrackingService.trackUserPreferenceUpdate(event);
    }
}
