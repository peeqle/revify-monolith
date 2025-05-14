package com.revify.monolith.resource.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@RequiredArgsConstructor
@EnableReactiveMongoRepositories
public class FilesConfig {

    private final MongoConverter mongoConverter;

    private final ReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory;

    private final MongoDatabaseFactory mongoDatabaseFactory;

    @Bean
    public GsonBuilder gsonBuilder() {
        return new GsonBuilder();
    }

    @Bean
    public Gson gson() {
        return gsonBuilder().create();
    }

    @Bean
    public ReactiveGridFsTemplate reactiveGridFsTemplate() {
        return new ReactiveGridFsTemplate(reactiveMongoDatabaseFactory, mongoConverter);
    }

    @Bean
    public GridFsTemplate gridFsTemplate() {
        return new GridFsTemplate(mongoDatabaseFactory, mongoConverter);
    }
}
