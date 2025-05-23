package com.revify.monolith.geo.service;

import com.google.gson.annotations.SerializedName;
import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.commons.geolocation.GeoLocation;
import com.revify.monolith.geo.model.Address;
import com.revify.monolith.geo.model.Place;
import com.revify.monolith.geo.model.UserGeolocation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;

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

    public void updateUserGeolocation(Double lat, Double lon) {
        if (lat != null && lon != null) {
            UserGeolocation latestUserGeolocation = findLatestUserGeolocation();
            if (latestUserGeolocation != null && !latestUserGeolocation.isWorthUpdating(lat, lon)) {
                return;
            }
            Place place = nominatimService.readGeolocationAddress(lat, lon);

            if (place != null) {
                GeoLocation geoLocation = mapGeolocation(place);
                geoLocation.setLocation(new GeoJsonPoint(lat, lon));
                if (latestUserGeolocation == null) {
                    latestUserGeolocation = new UserGeolocation();
                }
                latestUserGeolocation.setCurrent(geoLocation);
                latestUserGeolocation.setUserId(UserUtils.getUserId());
                latestUserGeolocation.setTimestamp(Instant.now().toEpochMilli());
                mongoTemplate.save(latestUserGeolocation);
            }
        }
    }

    private static GeoLocation mapGeolocation(Place place) {
        Address address = place.getAddress();

        GeoLocation geolocation = new GeoLocation();
        geolocation.setCountryCode(place.getAddress().getCountry_code());
        geolocation.setCountryName(address.getCountry());

        geolocation.setPlaceName(address.getCity() == null ? address.getVillage() == null ? address.getTown() == null ?
                "" : address.getTown() : address.getVillage() : address.getCity());
        geolocation.setPlaceDistrict(address.getRegion());
        geolocation.setStateName(address.getState());

        geolocation.setDisplayName(place.getDisplay_name());

        return geolocation;
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
