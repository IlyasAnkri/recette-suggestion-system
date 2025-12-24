package com.recipeadjuster.recipe.service;

import com.recipeadjuster.recipe.config.RedisConfig;
import com.recipeadjuster.shared.model.entity.Recipe;
import com.recipeadjuster.shared.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {
    RecipeCacheService.class,
    RedisConfig.class,
    RedisAutoConfiguration.class,
    CacheAutoConfiguration.class
})
@Testcontainers
class RecipeCacheServiceTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
        .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private RecipeCacheService recipeCacheService;

    @MockBean
    private RecipeRepository recipeRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        cacheManager.getCacheNames().forEach(cacheName -> 
            cacheManager.getCache(cacheName).clear());
    }

    @Test
    void shouldCacheRecipeOnFirstCall() {
        Recipe recipe = createTestRecipe("recipe-1", "Test Recipe");
        when(recipeRepository.findById("recipe-1")).thenReturn(Optional.of(recipe));

        Recipe result1 = recipeCacheService.getRecipeById("recipe-1");
        Recipe result2 = recipeCacheService.getRecipeById("recipe-1");

        assertThat(result1).isNotNull();
        assertThat(result1.getTitle()).isEqualTo("Test Recipe");
        assertThat(result2).isNotNull();
        
        verify(recipeRepository, times(1)).findById("recipe-1");
    }

    @Test
    void shouldReturnCachedRecipeOnSubsequentCalls() {
        Recipe recipe = createTestRecipe("recipe-2", "Cached Recipe");
        when(recipeRepository.findById("recipe-2")).thenReturn(Optional.of(recipe));

        recipeCacheService.getRecipeById("recipe-2");
        recipeCacheService.getRecipeById("recipe-2");
        recipeCacheService.getRecipeById("recipe-2");

        verify(recipeRepository, times(1)).findById("recipe-2");
    }

    @Test
    void shouldCacheDifferentRecipesSeparately() {
        Recipe recipe1 = createTestRecipe("recipe-1", "Recipe One");
        Recipe recipe2 = createTestRecipe("recipe-2", "Recipe Two");
        
        when(recipeRepository.findById("recipe-1")).thenReturn(Optional.of(recipe1));
        when(recipeRepository.findById("recipe-2")).thenReturn(Optional.of(recipe2));

        Recipe result1 = recipeCacheService.getRecipeById("recipe-1");
        Recipe result2 = recipeCacheService.getRecipeById("recipe-2");

        assertThat(result1.getTitle()).isEqualTo("Recipe One");
        assertThat(result2.getTitle()).isEqualTo("Recipe Two");
        
        verify(recipeRepository, times(1)).findById("recipe-1");
        verify(recipeRepository, times(1)).findById("recipe-2");
    }

    @Test
    void shouldHandleCacheMiss() {
        when(recipeRepository.findById(anyString())).thenReturn(Optional.empty());

        try {
            recipeCacheService.getRecipeById("non-existent");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("Recipe not found");
        }

        verify(recipeRepository, times(1)).findById("non-existent");
    }

    @Test
    void shouldEvictCacheAfterClear() {
        Recipe recipe = createTestRecipe("recipe-3", "Evictable Recipe");
        when(recipeRepository.findById("recipe-3")).thenReturn(Optional.of(recipe));

        recipeCacheService.getRecipeById("recipe-3");
        
        cacheManager.getCache("popularRecipes").clear();
        
        recipeCacheService.getRecipeById("recipe-3");

        verify(recipeRepository, times(2)).findById("recipe-3");
    }

    private Recipe createTestRecipe(String id, String title) {
        Recipe recipe = new Recipe();
        recipe.setId(id);
        recipe.setTitle(title);
        recipe.setSlug(title.toLowerCase().replace(" ", "-"));
        recipe.setDescription("Test description");
        recipe.setCuisine("Italian");
        recipe.setDifficulty(Recipe.Difficulty.EASY);
        recipe.setPrepTime(10);
        recipe.setCookTime(20);
        recipe.setServings(4);
        recipe.setIsApproved(true);
        return recipe;
    }
}
