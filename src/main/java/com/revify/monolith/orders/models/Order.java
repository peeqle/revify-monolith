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

    @Positive
    @NotNull(message = "Delivery time must persist")
    private Long deliveryTimeEnd;

    private Long createdAt;
    private Long updatedAt;

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
            return particle;
        }
        return null;
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
