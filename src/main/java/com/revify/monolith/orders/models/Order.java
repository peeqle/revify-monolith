package com.revify.monolith.orders.models;

import com.revify.monolith.commons.models.orders.OrderAdditionalStatus;
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

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

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

    private Map<Long, Long> couriersInvolved = new HashMap<>();

    @Positive
    @NotNull(message = "Delivery time must persist")
    private Long deliveryTimeEnd;

    private Long createdAt;
    private Long updatedAt;

    private Boolean isSuspended = false;

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
            couriersInvolved.put(particle.getCourierId(), Instant.now().toEpochMilli());
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
}
