package com.recipeadjuster.recipe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(
    exclude = {DataSourceAutoConfiguration.class},
    scanBasePackages = {"com.recipeadjuster.recipe", "com.recipeadjuster.shared"}
)
@EnableDiscoveryClient
@EnableMongoRepositories(basePackages = "com.recipeadjuster.shared.repository")
@EnableCaching
public class RecipeSearchServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RecipeSearchServiceApplication.class, args);
    }
}
