package com.recipeadjuster.recipedatabase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
public class RecipeDatabaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(RecipeDatabaseApplication.class, args);
    }
}
