package com.revify.monolith.config.data;

import com.revify.monolith.geo.model.UserGeolocation;
import com.revify.monolith.items.model.item.Item;
import com.revify.monolith.items.model.item.composite.CompositeItem;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MongoIndexesInitializer {

    private final ReactiveMongoTemplate mongoTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void initIndexes() {
        mongoTemplate.indexOps(UserGeolocation.class)
                .ensureIndex(new Index("userId", Sort.Direction.DESC))
                .subscribe(success -> System.out.println("Index created: " + success),
                        error -> System.err.println("Error creating index: " + error));

        mongoTemplate.indexOps(UserGeolocation.class)
                .ensureIndex(new GeospatialIndex("current.location").typed(GeoSpatialIndexType.GEO_2DSPHERE))
                .subscribe(success -> System.out.println("Index created: " + success),
                        error -> System.err.println("Error creating index: " + error));

        // item indexes
        mongoTemplate.indexOps(Item.class)
                .ensureIndex(new GeospatialIndex("itemDescription.destination.location").typed(GeoSpatialIndexType.GEO_2DSPHERE))
                .subscribe(success -> System.out.println("Index created: " + success),
                        error -> System.err.println("Error creating index: " + error));

        mongoTemplate.indexOps(CompositeItem.class)
                .ensureIndex(new GeospatialIndex("destination.location").typed(GeoSpatialIndexType.GEO_2DSPHERE))
                .subscribe(success -> System.out.println("Index created: " + success),
                        error -> System.err.println("Error creating index: " + error));
    }
}