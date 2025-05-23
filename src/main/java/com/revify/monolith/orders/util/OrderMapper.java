package com.revify.monolith.orders.util;


import com.revify.monolith.commons.models.orders.OrderCreationDTO;
import com.revify.monolith.commons.models.orders.OrderDTO;
import com.revify.monolith.orders.models.Order;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OrderMapper {

    public static Order to(OrderCreationDTO orderCreationDTO) {
        if (orderCreationDTO == null) {
            throw new IllegalArgumentException("OrderCreationDTO cannot be null");
        }

        var currentTime = System.currentTimeMillis();

        return Order.builder()
                .createdAt(currentTime)
                .updatedAt(currentTime)
                .status(orderCreationDTO.status())
                .itemId(orderCreationDTO.itemId())
                .deliveryTimeEnd(orderCreationDTO.deliveryTimeEnd())
                .additionalStatus(orderCreationDTO.additionalStatus())
                .receiverId(orderCreationDTO.receiverId())
                .shipmentParticle(OrderShipmentParticleMapper.to(orderCreationDTO.shipmentParticle()))
                .build();
    }
}
