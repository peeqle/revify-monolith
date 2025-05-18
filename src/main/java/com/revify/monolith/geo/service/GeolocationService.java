package com.revify.monolith.geo.service;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.revify.monolith.commons.geolocation.GeoLocation;
import com.revify.monolith.geo.model.Address;
import com.revify.monolith.geo.model.Place;
import com.revify.monolith.geo.model.UserGeolocation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeolocationService {

    private final MongoTemplate mongoTemplate;

    private final NominatimService nominatimService;

    public UserGeolocation resolveGeolocation(Long senderId, Long timestamp, Double lat, Double lon) {
        if (lat != null && lon != null) {
            Place place = nominatimService.readGeolocationAddress(lat, lon);
            if (place != null) {
                GeoLocation geoLocation = mapGeolocation(place);
                geoLocation.setLocation(new GeoJsonPoint(lat, lon));

                UserGeolocation userGeolocation = new UserGeolocation();
                userGeolocation.setCurrent(geoLocation);
                userGeolocation.setUserId(senderId);
                userGeolocation.setTimestamp(timestamp);
                return mongoTemplate.save(userGeolocation);
            }
        }
        return null;
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
}
