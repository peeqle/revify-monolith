package com.revify.monolith.commons.models.orders;


import com.revify.monolith.orders.models.Order;

public record OrderDTO(
        String id,
        Long receiverId,
        String itemId,
        OrderShipmentStatus status,
        OrderAdditionalStatus additionalStatus,
        Long deliveryTimeEnd,
        OrderShipmentParticle shipmentParticle,
        Boolean isSuspended
) {
    public static OrderDTO from(Order order) {
        return new OrderDTO(order.getId().toHexString(), order.getReceiverId(), order.getItemId(), order.getStatus(),
                order.getAdditionalStatus(), order.getDeliveryTimeEnd(), order.getShipmentParticle(), order.getIsSuspended());
    }
}