package com.revify.monolith.commons.models.orders;

import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.geo.model.GeoLocation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@Builder
@AllArgsConstructor
public class OrderShipmentParticle {
    private final Long courierId;
    private Price price;
    private Price previousPrice;
    //todo change to ids
    private final GeoLocation from;
    private GeoLocation to;
    private Boolean isSplit;
    private final Long deliveryTimeEstimated;
    private OrderShipmentParticle next;
    private OrderShipmentParticle previous;
}
