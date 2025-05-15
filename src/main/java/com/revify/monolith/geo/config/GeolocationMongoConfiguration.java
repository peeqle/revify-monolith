package com.revify.monolith.geo.config;

import com.mongodb.reactivestreams.client.MongoClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@RequiredArgsConstructor

@EnableReactiveMongoRepositories
public class GeolocationMongoConfiguration {

    public static final String DATABASE_NAME = "geolocation";

    private final MongoClient mongoClient;

    @Bean(name = "geolocationMongoTemplate")
    public ReactiveMongoTemplate geolocationMongoTemplate() {
        return new ReactiveMongoTemplate(mongoClient, DATABASE_NAME);
    }
}
