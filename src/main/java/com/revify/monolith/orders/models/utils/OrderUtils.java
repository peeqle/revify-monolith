package com.revify.monolith.orders.models.utils;

import com.revify.monolith.commons.models.orders.OrderShipmentParticle;
import io.vavr.Tuple;
import io.vavr.Tuple2;

public class OrderUtils {

    public static Tuple2<Integer, OrderShipmentParticle> findShipmentParticle(Long courierId, OrderShipmentParticle head) {
        return findShipmentParticle(0, head, courierId);
    }


    public static Tuple2<Integer, OrderShipmentParticle> findShipmentParticle(int index, OrderShipmentParticle current, Long courierId) {
        if (current == null) {
            return Tuple.of(index, null);
        }

        if (current.getCourierId().equals(courierId)) {
            return Tuple.of(index, current);
        }
        return findShipmentParticle(index + 1, current.getNext(), courierId);
    }
}
