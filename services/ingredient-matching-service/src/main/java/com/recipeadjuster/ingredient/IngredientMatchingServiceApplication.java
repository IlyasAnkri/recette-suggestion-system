package com.recipeadjuster.ingredient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(
    exclude = {DataSourceAutoConfiguration.class},
    scanBasePackages = {"com.recipeadjuster.ingredient", "com.recipeadjuster.shared"}
)
@EnableDiscoveryClient
@EnableMongoRepositories(basePackages = "com.recipeadjuster.shared.repository")
public class IngredientMatchingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(IngredientMatchingServiceApplication.class, args);
    }
}
