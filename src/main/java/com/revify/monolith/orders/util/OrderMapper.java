package com.revify.monolith.orders.util;


import com.revify.monolith.commons.models.orders.OrderCreationDTO;
import com.revify.monolith.commons.models.orders.OrderDTO;
import com.revify.monolith.orders.models.Order;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OrderMapper {

    public static OrderDTO from(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        return new OrderDTO(
                order.getId().toHexString(),
                order.getReceiverId(),
                order.getItemId(),
                order.getStatus(),
                order.getAdditionalStatus(),
                order.getDeliveryTimeEnd(),
                OrderShipmentParticleMapper.from(order.getShipmentParticle())
        );
    }

    public static Order to(OrderDTO orderDTO) {
        if (orderDTO == null) {
            throw new IllegalArgumentException("OrderDTO cannot be null");
        }

        var currentTime = System.currentTimeMillis();

        return Order.builder()
                .createdAt(currentTime)
                .updatedAt(currentTime)
                .status(orderDTO.status())
                .itemId(orderDTO.itemId())
                .deliveryTimeEnd(orderDTO.deliveryTimeEnd())
                .additionalStatus(orderDTO.additionalStatus())
                .receiverId(orderDTO.receiverId())
                .shipmentParticle(OrderShipmentParticleMapper.to(orderDTO.shipmentParticle()))
                .build();
    }

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
