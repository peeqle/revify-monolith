package com.revify.monolith.commons.models.orders;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderStatusUpdateNotification {
    private String orderId;
    private String itemId;
    private Long receiverId;
    private Long deliveryTimeEnd;
    private OrderShipmentStatus status;
    private OrderAdditionalStatus additionalStatus;
}