package com.revify.monolith.commons.models.orders;

import lombok.Builder;

@Builder
public record OrderCreationDTO (
         Long receiverId,
         String itemId,
         OrderShipmentStatus status,
         OrderAdditionalStatus additionalStatus,
         OrderShipmentParticle shipmentParticle,
         Long deliveryTimeEnd
) { }