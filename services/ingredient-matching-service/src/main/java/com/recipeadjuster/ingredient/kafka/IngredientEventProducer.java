package com.recipeadjuster.ingredient.kafka;

import com.recipeadjuster.shared.config.KafkaTopics;
import com.recipeadjuster.shared.event.IngredientSubmittedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngredientEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishIngredientSubmitted(IngredientSubmittedEvent event) {
        log.info("Publishing ingredient.submitted event: {}", event.getEventId());
        
        kafkaTemplate.send(
            KafkaTopics.INGREDIENT_SUBMITTED,
            event.getEventId(),
            event
        );
        
        log.debug("Event published successfully to topic: {}", KafkaTopics.INGREDIENT_SUBMITTED);
    }
}
