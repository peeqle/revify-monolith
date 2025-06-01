package com.revify.monolith.commons.models.orders;

import lombok.Builder;

import java.util.Collection;

@Builder
public record OrderCreationDTO(
        Collection<Long> receivers,
        Collection<String> items,
        OrderShipmentStatus status,
        OrderAdditionalStatus additionalStatus,
        OrderShipmentParticle shipmentParticle,
        Long paymentsCutoff,
        Long deliveryTimeEnd,
        Boolean isShoplift) { }