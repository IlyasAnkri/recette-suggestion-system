package com.recipeadjuster.recipedatabase.config;

import com.recipeadjuster.recipedatabase.service.KafkaProducerService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {
    
    @Bean
    @Primary
    public KafkaProducerService kafkaProducerService() {
        return mock(KafkaProducerService.class);
    }
}
