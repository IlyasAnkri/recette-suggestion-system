package com.recipeadjuster.substitution.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    @ConditionalOnProperty(name = "spring.ai.openai.chat.enabled", havingValue = "true", matchIfMissing = true)
    public ChatModel chatModel(OpenAiChatModel openAiChatModel) {
        return openAiChatModel;
    }
    
    @Bean
    @ConditionalOnProperty(name = "spring.ai.openai.chat.enabled", havingValue = "false")
    public ChatModel mockChatModel() {
        return new ChatModel() {
            @Override
            public org.springframework.ai.chat.model.ChatResponse call(org.springframework.ai.chat.prompt.Prompt prompt) {
                throw new UnsupportedOperationException("AI not configured - using fallback service");
            }
            
            @Override
            public reactor.core.publisher.Flux<org.springframework.ai.chat.model.ChatResponse> stream(org.springframework.ai.chat.prompt.Prompt prompt) {
                return reactor.core.publisher.Flux.error(new UnsupportedOperationException("AI not configured"));
            }
            
            @Override
            public org.springframework.ai.chat.prompt.ChatOptions getDefaultOptions() {
                return null;
            }
        };
    }
}