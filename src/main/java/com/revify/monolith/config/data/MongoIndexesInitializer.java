package com.revify.monolith.config.data;

import com.revify.monolith.geo.model.StoredGeoLocation;
import com.revify.monolith.geo.model.UserGeolocation;
import com.revify.monolith.items.model.item.Item;
import com.revify.monolith.items.model.item.composite.CompositeItem;
import com.revify.monolith.orders.models.PathSegment;
import com.revify.monolith.shoplift.model.Shoplift;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MongoIndexesInitializer {

    private final MongoTemplate mongoTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void initIndexes() {
        mongoTemplate.indexOps(UserGeolocation.class)
                .ensureIndex(new Index("userId", Sort.Direction.DESC));

        mongoTemplate.indexOps(UserGeolocation.class)
                .ensureIndex(new GeospatialIndex("geoLocation.location").typed(GeoSpatialIndexType.GEO_2DSPHERE));

        mongoTemplate.indexOps(StoredGeoLocation.class)
                .ensureIndex(new GeospatialIndex("geoLocation.location").typed(GeoSpatialIndexType.GEO_2DSPHERE));

        // item indexes
        mongoTemplate.indexOps(Item.class)
                .ensureIndex(new GeospatialIndex("itemDescription.destination.location").typed(GeoSpatialIndexType.GEO_2DSPHERE));

        mongoTemplate.indexOps(CompositeItem.class)
                .ensureIndex(new GeospatialIndex("destination.location").typed(GeoSpatialIndexType.GEO_2DSPHERE));

        //paths indexes
        mongoTemplate.indexOps(PathSegment.class)
                .ensureIndex(new GeospatialIndex("particle.from.location").typed(GeoSpatialIndexType.GEO_2DSPHERE));
        mongoTemplate.indexOps(PathSegment.class)
                .ensureIndex(new GeospatialIndex("particle.to.location").typed(GeoSpatialIndexType.GEO_2DSPHERE));

        mongoTemplate.indexOps(Shoplift.class)
                .ensureIndex(new GeospatialIndex("particle.destination.location").typed(GeoSpatialIndexType.GEO_2DSPHERE));
    }
}