package com.revify.monolith.orders.models;

import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.commons.models.orders.OrderAdditionalStatus;
import com.revify.monolith.commons.models.orders.OrderCreationDTO;
import com.revify.monolith.commons.models.orders.OrderShipmentParticle;
import com.revify.monolith.commons.models.orders.OrderShipmentStatus;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.*;

@Data
@Builder
@Document(collection = "order")
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    private ObjectId id;

    @NotNull
    private Long receiverId;

    @NotNull
    private String itemId;

    private OrderShipmentStatus status = OrderShipmentStatus.CREATED;
    private OrderAdditionalStatus additionalStatus;

    @NotNull(message = "Shipment particle cannot be null")
    private OrderShipmentParticle shipmentParticle;

    private Set<Long> couriersInvolved = new HashSet<>();

    @Positive
    @NotNull(message = "Delivery time must persist")
    private Long deliveryTimeEnd;

    private Long createdAt;
    private Long updatedAt;

    private Boolean isSuspended = false;
    private Boolean isPaid = false;

    public void addShipmentParticle(OrderShipmentParticle particle) {
        if (particle != null) {
            OrderShipmentParticle last = this.getLast();

            //only case is when head is null
            if (last != null) {
                particle.setPrevious(last);
                last.setNext(particle);
            } else {
                this.shipmentParticle = particle;
            }

            couriersInvolved.add(particle.getCourierId());
        }
    }

    public Tuple2<Integer, OrderShipmentParticle> removeShipmentParticle(Long courierId) {
        Tuple2<Integer, OrderShipmentParticle> particle = findShipmentParticle(0, this.shipmentParticle, courierId);
        OrderShipmentParticle forCourier = particle._2;

        if (forCourier != null) {
            OrderShipmentParticle previous = forCourier.getPrevious();
            OrderShipmentParticle next = forCourier.getNext();
            if (next != null) {
                next.setPrevious(previous);
            }
            if (previous != null) {
                previous.setNext(next);
            }
            couriersInvolved.removeIf(courierId::equals);
            return particle;
        }
        return null;
    }

    public OrderShipmentParticle getLast() {
        var head = this.shipmentParticle;
        while (head != null && head.getNext() != null) {
            head = head.getNext();
        }
        return head;
    }
    /**index & particle
     * @param courierId
     * @return index & particle
     */
    public Tuple2<Integer, OrderShipmentParticle> findShipmentParticle(Long courierId) {
        return findShipmentParticle(0, this.shipmentParticle, courierId);
    }



    public Tuple2<Integer, OrderShipmentParticle> findShipmentParticle(int index, OrderShipmentParticle current, Long courierId) {
        if (current == null) {
            return Tuple.of(index, null);
        }

        if (current.getCourierId().equals(courierId)) {
            return Tuple.of(index, current);
        }
        return findShipmentParticle(index + 1, current.getNext(), courierId);
    }

    public static Order from(OrderCreationDTO creationDTO) {
        if (creationDTO == null) {
            throw new IllegalArgumentException("OrderCreationDTO cannot be null");
        }

        var currentTime = System.currentTimeMillis();

        Order build = Order.defaultBuild()
                .createdAt(currentTime)
                .updatedAt(currentTime)
                .status(creationDTO.status())
                .itemId(creationDTO.itemId())
                .deliveryTimeEnd(creationDTO.deliveryTimeEnd())
                .additionalStatus(creationDTO.additionalStatus())
                .receiverId(creationDTO.receiverId())
                .build();

        build.addShipmentParticle(creationDTO.shipmentParticle());
        return build;
    }

    public static Order.OrderBuilder defaultBuild() {
        return Order.builder()
                .couriersInvolved(new HashSet<>())
                .isPaid(false)
                .isSuspended(false)
                .status(OrderShipmentStatus.CREATED);
    }
}
