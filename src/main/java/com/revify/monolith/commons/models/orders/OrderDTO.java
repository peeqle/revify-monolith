package com.revify.monolith.commons.models.orders;

public record OrderDTO(
        String id,
        Long receiverId,
        String itemId,
        OrderShipmentStatus status,
        OrderAdditionalStatus additionalStatus,
        Long deliveryTimeEnd,
        OrderShipmentParticle shipmentParticle
) { }