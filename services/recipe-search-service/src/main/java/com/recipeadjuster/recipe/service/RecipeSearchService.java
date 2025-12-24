package com.recipeadjuster.recipe.service;

import com.recipeadjuster.recipe.dto.RecipeSearchRequest;
import com.recipeadjuster.recipe.dto.RecipeSearchResponse;
import com.recipeadjuster.shared.model.entity.Recipe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeSearchService {

    private final MongoTemplate mongoTemplate;

    @Cacheable(value = "recipeSearch", key = "#request.toString()")
    public RecipeSearchResponse searchRecipes(RecipeSearchRequest request) {
        Query query = buildQuery(request);
        
        long total = mongoTemplate.count(query, Recipe.class);
        
        query.with(PageRequest.of(
            request.getPage() - 1,
            request.getLimit(),
            getSort(request.getSort())
        ));
        
        List<Recipe> recipes = mongoTemplate.find(query, Recipe.class);
        
        List<RecipeSearchResponse.RecipeSummary> summaries = recipes.stream()
            .map(this::toSummary)
            .collect(Collectors.toList());
        
        int totalPages = (int) Math.ceil((double) total / request.getLimit());
        
        return RecipeSearchResponse.builder()
            .recipes(summaries)
            .pagination(RecipeSearchResponse.PaginationMetadata.builder()
                .page(request.getPage())
                .limit(request.getLimit())
                .total(total)
                .totalPages(totalPages)
                .build())
            .build();
    }

    private Query buildQuery(RecipeSearchRequest request) {
        Query query = new Query();
        
        query.addCriteria(Criteria.where("isApproved").is(true));
        
        if (request.getQuery() != null && !request.getQuery().isEmpty()) {
            query.addCriteria(TextCriteria.forDefaultLanguage().matching(request.getQuery()));
        }
        
        if (request.getCuisines() != null && !request.getCuisines().isEmpty()) {
            query.addCriteria(Criteria.where("cuisine").in(request.getCuisines()));
        }
        
        if (request.getDifficulty() != null) {
            query.addCriteria(Criteria.where("difficulty").is(Recipe.Difficulty.valueOf(request.getDifficulty().toUpperCase())));
        }
        
        if (request.getMaxPrepTime() != null) {
            query.addCriteria(Criteria.where("prepTime").lte(request.getMaxPrepTime()));
        }
        
        if (request.getMaxCookTime() != null) {
            query.addCriteria(Criteria.where("cookTime").lte(request.getMaxCookTime()));
        }
        
        if (request.getDietary() != null && !request.getDietary().isEmpty()) {
            query.addCriteria(Criteria.where("tags").all(request.getDietary()));
        }
        
        return query;
    }

    private Sort getSort(String sortBy) {
        return switch (sortBy) {
            case "newest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "cookTime" -> Sort.by(Sort.Direction.ASC, "cookTime");
            default -> Sort.by(Sort.Direction.DESC, "ratings.average");
        };
    }

    private RecipeSearchResponse.RecipeSummary toSummary(Recipe recipe) {
        return RecipeSearchResponse.RecipeSummary.builder()
            .id(recipe.getId())
            .title(recipe.getTitle())
            .description(recipe.getDescription())
            .thumbnail(recipe.getImageUrl())
            .prepTime(recipe.getPrepTime())
            .cookTime(recipe.getCookTime())
            .difficulty(recipe.getDifficulty().name())
            .rating(recipe.getRatings() != null ? recipe.getRatings().getAverage() : 0.0)
            .ratingCount(recipe.getRatings() != null ? recipe.getRatings().getCount() : 0)
            .build();
    }
}
