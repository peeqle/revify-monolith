package com.revify.monolith.items.config;

import com.revify.monolith.items.model.item.Item;
import com.revify.monolith.items.model.item.composite.CompositeItem;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeospatialIndex;

@Configuration
public class ItemsMongoIndexConfiguration {

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public ItemsMongoIndexConfiguration(@Qualifier("itemsMongoTemplate") ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initIndexes() {
        reactiveMongoTemplate.indexOps(Item.class)
                .ensureIndex(new GeospatialIndex("itemDescription.destination.location").typed(GeoSpatialIndexType.GEO_2DSPHERE))
                .subscribe(success -> System.out.println("Index created: " + success),
                        error -> System.err.println("Error creating index: " + error));

        reactiveMongoTemplate.indexOps(CompositeItem.class)
                .ensureIndex(new GeospatialIndex("destination.location").typed(GeoSpatialIndexType.GEO_2DSPHERE))
                .subscribe(success -> System.out.println("Index created: " + success),
                        error -> System.err.println("Error creating index: " + error));
    }
}
