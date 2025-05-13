package com.revify.monolith.geo.service;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.revify.monolith.commons.geolocation.GeoLocation;
import com.revify.monolith.geo.model.Address;
import com.revify.monolith.geo.model.Place;
import com.revify.monolith.geo.model.UserGeolocation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GeolocationService {

    private final ReactiveMongoTemplate mongoTemplate;

    private final NominatimService nominatimService;

    private final Gson gson;

    public Mono<UserGeolocation> saveParsedFromWS(String request) {
        GeolocationConsumerRecord geo = gson.fromJson(request, GeolocationConsumerRecord.class);
        if (geo != null) {
            Payload payload = gson.fromJson(geo.getPayload(), Payload.class);

            if (payload.getLat() != null && payload.getLon() != null) {
                return nominatimService.readGeolocationAddress(payload.getLat(), payload.getLon())
                        .map(GeolocationService::mapGeolocation)
                        .map(geoLocation -> {
                            geoLocation.setLocation(new GeoJsonPoint(payload.getLon(), payload.getLat()));
                            return geoLocation;
                        })
                        .flatMap(geoLocation -> {
                            UserGeolocation userGeolocation = new UserGeolocation();
                            userGeolocation.setCurrent(geoLocation);
                            userGeolocation.setUserId(geo.getSenderId());
                            userGeolocation.setTimestamp(geo.getTimestamp());
                            return mongoTemplate.save(userGeolocation);
                        });
            }
        }
        return Mono.empty();
    }

    @Data
    private static class GeolocationConsumerRecord {
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
