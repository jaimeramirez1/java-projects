package com.gmapex.orderservice.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MongoHealthCheck implements ApplicationRunner {

    private final MongoTemplate mongoTemplate;

    public MongoHealthCheck(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            log.info("Testing MongoDB connection...");
            mongoTemplate.getDb().listCollectionNames().first();
            log.info("✅ MongoDB connection successful!");
        } catch (Exception e) {
            log.error("❌ MongoDB connection failed: {}", e.getMessage());
            throw new RuntimeException("Failed to connect to MongoDB", e);
        }
    }
}