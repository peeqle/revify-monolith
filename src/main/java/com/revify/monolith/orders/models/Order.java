package com.revify.monolith.orders.models;

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

import java.util.HashSet;
import java.util.Set;

import static com.revify.monolith.orders.models.utils.OrderUtils.findShipmentParticle;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Document(collection = "order")
public class Order {
    @Id
    private ObjectId id;

    @NotNull
    private Set<Long> receivers;

    @NotNull
    private Set<String> items;

    private OrderShipmentStatus status = OrderShipmentStatus.CREATED;
    private OrderAdditionalStatus additionalStatus;

    @NotNull(message = "Shipment particle cannot be null")
    private OrderShipmentParticle shipmentParticle;

    private Set<Long> couriersInvolved = new HashSet<>();

    @Positive
    @NotNull(message = "Delivery time must persist")
    private Long deliveryTimeEnd;

    @Positive
    private Long paymentsCutoff;

    private Long createdAt;
    private Long updatedAt;

    private boolean isSuspended = false;
    private boolean isPaid = false;
    private boolean isShoplift = false;

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


    public static Order from(OrderCreationDTO creationDTO) {
        if (creationDTO == null) {
            throw new IllegalArgumentException("OrderCreationDTO cannot be null");
        }

        var currentTime = System.currentTimeMillis();

        Order build = Order.defaultBuild()
                .createdAt(currentTime)
                .updatedAt(currentTime)
                .status(creationDTO.status())
                .items(new HashSet<>(creationDTO.items()))
                .receivers(new HashSet<>(creationDTO.receivers()))
                .deliveryTimeEnd(creationDTO.deliveryTimeEnd())
                .additionalStatus(creationDTO.additionalStatus())
                .paymentsCutoff(creationDTO.paymentsCutoff())
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