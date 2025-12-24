package com.recipeadjuster.analytics.controller;

import com.recipeadjuster.analytics.model.dto.DashboardResponse;
import com.recipeadjuster.analytics.model.dto.PopularRecipeResponse;
import com.recipeadjuster.analytics.service.AnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnalyticsController.class)
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsService analyticsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnDashboard() throws Exception {
        DashboardResponse dashboard = DashboardResponse.builder()
            .dailyActiveUsers(150)
            .totalSearchesToday(500)
            .totalSubstitutionsToday(75)
            .avgIngredientsPerSearch(BigDecimal.valueOf(3.5))
            .totalRecipeViews(1200)
            .build();

        when(analyticsService.getDashboard(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(dashboard);

        mockMvc.perform(get("/api/v1/analytics/dashboard"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dailyActiveUsers").value(150))
            .andExpect(jsonPath("$.totalSearchesToday").value(500))
            .andExpect(jsonPath("$.totalSubstitutionsToday").value(75));
    }

    @Test
    void shouldReturn403WhenNotAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/dashboard"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnPopularRecipes() throws Exception {
        List<PopularRecipeResponse> recipes = List.of(
            PopularRecipeResponse.builder()
                .recipeId("recipe-1")
                .recipeName("Chicken Pasta")
                .viewCount(250)
                .rank(1)
                .build(),
            PopularRecipeResponse.builder()
                .recipeId("recipe-2")
                .recipeName("Beef Stew")
                .viewCount(180)
                .rank(2)
                .build()
        );

        when(analyticsService.getPopularRecipes()).thenReturn(recipes);

        mockMvc.perform(get("/api/v1/analytics/recipes/popular"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].recipeId").value("recipe-1"))
            .andExpect(jsonPath("$[0].viewCount").value(250))
            .andExpect(jsonPath("$[1].recipeId").value("recipe-2"));
    }
}
