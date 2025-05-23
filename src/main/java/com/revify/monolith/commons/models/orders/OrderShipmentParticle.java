package com.revify.monolith.commons.models.orders;

import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.geo.model.GeoLocation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OrderShipmentParticle {
    private final Long courierId;
    private final Price price;
    //todo change to ids
    private final GeoLocation to;
    private final GeoLocation from;
    private final Long deliveryTimeEstimated;
    private OrderShipmentParticle next;
    private OrderShipmentParticle previous;
}
