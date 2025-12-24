package com.recipeadjuster.recipedatabase.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipeadjuster.recipedatabase.event.RecipeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    
    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);
    private static final String RECIPE_TOPIC = "recipe-events";
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    
    public void publishRecipeCreated(RecipeEvent event) {
        publishEvent("recipe.created", event);
    }
    
    public void publishRecipeUpdated(RecipeEvent event) {
        publishEvent("recipe.updated", event);
    }
    
    private void publishEvent(String eventType, RecipeEvent event) {
        try {
            event.setEventType(eventType);
            String eventJson = objectMapper.writeValueAsString(event);
            
            kafkaTemplate.send(RECIPE_TOPIC, event.getRecipeId(), eventJson)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Published {} event for recipe: {}", eventType, event.getRecipeId());
                    } else {
                        log.error("Failed to publish {} event for recipe: {}", eventType, event.getRecipeId(), ex);
                    }
                });
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event", e);
        }
    }
}
