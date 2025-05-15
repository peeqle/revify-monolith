package com.revify.monolith.items.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.revify.monolith.items.service.item.ItemReadService;
import com.revify.monolith.user.service.ReadUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@RequiredArgsConstructor


@EnableReactiveMongoRepositories
public class ItemsConfiguration {

    public static final String DATABASE_NAME = "items";

    private final MongoClient mongoClient;


    @Bean(name = "itemsMongoTemplate")
    public ReactiveMongoTemplate itemsMongoTemplate() {
        return new ReactiveMongoTemplate(mongoClient, DATABASE_NAME);
    }
}

