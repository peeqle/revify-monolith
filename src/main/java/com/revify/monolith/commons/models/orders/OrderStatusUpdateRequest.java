package com.revify.monolith.commons.models.orders;

public record OrderStatusUpdateRequest(
    String orderId,
    OrderShipmentStatus newStatus,
    OrderAdditionalStatus additionalStatus
) { }
