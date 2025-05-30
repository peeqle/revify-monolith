package com.revify.monolith.geo.service;

import com.google.gson.annotations.SerializedName;
import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.geo.model.GeoLocation;
import com.revify.monolith.geo.model.Place;
import com.revify.monolith.geo.model.StoredGeoLocation;
import com.revify.monolith.geo.model.UserGeolocation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

import static com.revify.monolith.geo.GeolocationUtils.haversineDistance;
import static com.revify.monolith.geo.GeolocationUtils.mapGeolocation;

@Service
@RequiredArgsConstructor
public class GeolocationService {

    private final MongoTemplate mongoTemplate;

    private final NominatimService nominatimService;

    public UserGeolocation findLatestUserGeolocation() {
        return mongoTemplate.findOne(Query
                .query(Criteria.where("userId").is(UserUtils.getUserId())), UserGeolocation.class);
    }

    public UserGeolocation findLatestUserGeolocation(Long userId) {
        return mongoTemplate.findOne(Query
                .query(Criteria.where("userId").is(userId)), UserGeolocation.class);
    }

    public GeoLocation findById(ObjectId geolocationId) {
        Query query = Query.query(Criteria.where("_id").is(geolocationId));
        return mongoTemplate.findOne(query, GeoLocation.class);
    }

    public GeoLocation resolveLocation(Double latitude, Double longitude) {
        StoredGeoLocation forCoordinates = findForCoordinates(latitude, longitude);
        if (forCoordinates == null) {
            Place place = nominatimService.readGeolocationAddress(latitude, longitude);
            if (place != null) {
                GeoLocation geoLocation = mapGeolocation(place);
                geoLocation.setLocation(new GeoJsonPoint(longitude, latitude));

                forCoordinates = mongoTemplate.save(StoredGeoLocation.with(geoLocation));
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot resolve geolocation for: " + latitude + ", " + longitude);
            }
        }
        return forCoordinates.getGeoLocation();
    }

    public void updateUserGeolocation(Double lat, Double lon) {
        if (lat != null && lon != null) {
            UserGeolocation latestUserGeolocation = findLatestUserGeolocation();
            if (latestUserGeolocation != null) {
                GeoLocation geoLocation = latestUserGeolocation.getGeoLocation();
                if (geoLocation != null && geoLocation.getLocation() != null
                        && haversineDistance(lat, lon, geoLocation.getLocation().getY(), geoLocation.getLocation().getX()) < 5) {
                    return;
                }
            }

            GeoLocation location = resolveLocation(lat, lon);

            if (location != null) {
                if (latestUserGeolocation == null) {
                    latestUserGeolocation = new UserGeolocation();
                }
                latestUserGeolocation.setGeoLocation(location);
                latestUserGeolocation.setUserId(UserUtils.getUserId());
                latestUserGeolocation.setTimestamp(Instant.now().toEpochMilli());
                mongoTemplate.save(latestUserGeolocation);
            }
        }
    }

    public StoredGeoLocation findForCoordinates(Double lat, Double lon) {
        Query query = Query.query(Criteria.where("geoLocation.location").near(new GeoJsonPoint(lon, lat)).maxDistance(100.0));
        return mongoTemplate.findOne(query, StoredGeoLocation.class);
    }

    @Data
    public static class GeolocationConsumerRecord {

        private String topic;
        @SerializedName("sender_id")
        private Long senderId;
        private Long timestamp;
        private String payload;
    }

    @Data
    public static class Payload {
        private Double lat;
        private Double lon;
    }
}
