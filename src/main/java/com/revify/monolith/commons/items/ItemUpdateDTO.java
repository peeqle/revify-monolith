package com.revify.monolith.commons.items;

import com.revify.monolith.geo.model.GeoLocation;

import java.math.BigDecimal;


public record ItemUpdateDTO(String id, String title, String description,
                            BigDecimal amount, String currency,
                            GeoLocation geoLocation) {
}
