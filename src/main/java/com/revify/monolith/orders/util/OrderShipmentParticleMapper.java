package com.revify.monolith.orders.util;


import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.commons.geolocation.GeoLocation;
import com.revify.monolith.commons.models.orders.OrderShipmentParticle;

public class OrderShipmentParticleMapper {

    public static OrderShipmentParticle from(OrderShipmentParticle particle) {
        return OrderShipmentParticle.builder()
                .from(mapGeoLocation(particle.getFrom()))
                .to(mapGeoLocation(particle.getTo()))
                .price(mapPrice(particle.getPrice()))
                .deliveryTimeEstimated(particle.getDeliveryTimeEstimated())
                .next(particle.getNext())
                .previous(particle.getPrevious())
                .build();
    }

    public static OrderShipmentParticle to(OrderShipmentParticle particleDTO) {
        if (particleDTO == null) {
            return null;
        }

        return OrderShipmentParticle.builder()
                .from(mapGeoLocation(particleDTO.getFrom()))
                .to(mapGeoLocation(particleDTO.getTo()))
                .price(mapPrice(particleDTO.getPrice()))
                .deliveryTimeEstimated(particleDTO.getDeliveryTimeEstimated())
                .next(particleDTO.getNext())
                .previous(particleDTO.getPrevious())
                .build();
    }

    private static GeoLocation mapGeoLocation(GeoLocation geoLocation) {
        if (geoLocation == null) {
            return null;
        }

        return GeoLocation.builder()
                .countryCode(geoLocation.getCountryCode())
                .countryName(geoLocation.getCountryName())
                .placeName(geoLocation.getPlaceName())
                .location(geoLocation.getLocation())
                .build();
    }

    private static Price mapPrice(Price price) {
        if (price == null) {
            return null;
        }

        return new Price.Builder()
                .withAmount(price.getAmount())
                .withCurrency(price.getCurrency())
                .build();
    }
}
