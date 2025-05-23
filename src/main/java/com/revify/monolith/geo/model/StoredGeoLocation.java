package com.revify.monolith.geo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Document("geolocation")
public class StoredGeoLocation implements Serializable {

    @Id
    private ObjectId id;

    private GeoLocation geoLocation;
    private Long createdAt;

    public static StoredGeoLocation with(GeoLocation geoLocation) {
        StoredGeoLocation location = new StoredGeoLocation();
        location.setGeoLocation(geoLocation);
        location.setCreatedAt(Instant.now().toEpochMilli());

        return location;
    }
}
