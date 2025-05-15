package com.revify.monolith.bid;

import com.mongodb.reactivestreams.client.MongoClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@RequiredArgsConstructor


@EnableReactiveMongoRepositories
public class BidMongoConfiguration {

    public static final String DATABASE_NAME = "bids";

    private final MongoClient mongoClient;


    @Bean(name = "bidsMongoTemplate")
    public ReactiveMongoTemplate bidsMongoTemplate() {
        return new ReactiveMongoTemplate(mongoClient, DATABASE_NAME);
    }
}

