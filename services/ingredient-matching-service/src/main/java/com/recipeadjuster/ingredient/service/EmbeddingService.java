package com.recipeadjuster.ingredient.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmbeddingService {

    private static final int EMBEDDING_DIMENSIONS = 384;
    private final Random random = new Random(42);

    public List<Double> embed(String text) {
        log.debug("Generating embedding for text: {}", text);
        return generateMockEmbedding(text);
    }

    public List<List<Double>> embedBatch(List<String> texts) {
        return texts.stream()
            .map(this::embed)
            .collect(Collectors.toList());
    }

    private List<Double> generateMockEmbedding(String text) {
        random.setSeed(text.hashCode());
        Double[] embedding = new Double[EMBEDDING_DIMENSIONS];
        for (int i = 0; i < EMBEDDING_DIMENSIONS; i++) {
            embedding[i] = random.nextGaussian();
        }
        return Arrays.asList(embedding);
    }
}
