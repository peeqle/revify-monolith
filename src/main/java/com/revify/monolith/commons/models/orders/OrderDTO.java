package com.revify.monolith.commons.models.orders;


import com.revify.monolith.orders.models.Order;

import java.util.Collection;

public record OrderDTO(
        String id,
        Collection<Long> receivers,
        Collection<String> items,
        OrderShipmentStatus status,
        OrderAdditionalStatus additionalStatus,
        Long deliveryTimeEnd,
        OrderShipmentParticle shipmentParticle,
        Boolean isSuspended,
        Boolean isPaid,
        Boolean isShoplift
) {
    public static OrderDTO from(Order order) {
        return new OrderDTO(order.getId().toHexString(), order.getReceivers(), order.getItems(), order.getStatus(),
                order.getAdditionalStatus(), order.getDeliveryTimeEnd(), order.getShipmentParticle(), order.isSuspended(),
                order.isPaid(), order.isShoplift());
    }
}