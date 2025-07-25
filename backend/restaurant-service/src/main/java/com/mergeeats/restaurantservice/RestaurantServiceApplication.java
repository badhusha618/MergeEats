package com.mergeeats.restaurantservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(scanBasePackages = {"com.mergeeats.restaurantservice", "com.mergeeats.common"})
@EnableMongoAuditing
@EnableKafka
public class RestaurantServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestaurantServiceApplication.class, args);
    }
}