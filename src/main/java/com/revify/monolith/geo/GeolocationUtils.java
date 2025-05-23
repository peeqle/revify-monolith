package com.revify.monolith.geo;

import com.revify.monolith.geo.model.Address;
import com.revify.monolith.geo.model.GeoLocation;
import com.revify.monolith.geo.model.Place;

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

        return geolocation;
    }


    /**
     * Returns KM
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return
     */
    public static double haversineDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int R = 6371; // Earth's radius in km
        double dLat = (lat2 - lat1) * Math.PI / 180;
        double dLon = (lon2 - lon1) * Math.PI / 180;
        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
