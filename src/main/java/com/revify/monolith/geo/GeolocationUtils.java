package com.revify.monolith.geo;

import com.revify.monolith.geo.model.GeoLocation;
import com.revify.monolith.geo.model.Address;
import com.revify.monolith.geo.model.Place;

import java.time.Instant;

public class GeolocationUtils {
    public static GeoLocation mapGeolocation(Place place) {
        Address address = place.getAddress();

        GeoLocation geolocation = new GeoLocation();
        geolocation.setCountryCode(place.getAddress().getCountry_code());
        geolocation.setCountryName(address.getCountry());

        geolocation.setPlaceName(address.getCity() == null ? address.getVillage() == null ? address.getTown() == null ?
                "" : address.getTown() : address.getVillage() : address.getCity());
        geolocation.setPlaceDistrict(address.getRegion());
        geolocation.setStateName(address.getState());

        geolocation.setDisplayName(place.getDisplay_name());
        geolocation.setCreatedAt(Instant.now().toEpochMilli());

        return geolocation;
    }
}
