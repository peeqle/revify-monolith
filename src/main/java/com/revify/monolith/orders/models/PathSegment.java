package com.revify.monolith.orders.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.revify.monolith.commons.models.orders.OrderShipmentParticle;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Data
@Document("path_segment")
public class PathSegment {
    @Id
    private ObjectId id;

    @DocumentReference
    @JsonIgnore
    private Order order;
    private Long receiverId;
    private Long courierId;
    private Long acceptedCourierId;
    private OrderShipmentParticle particle;

    private Long createdAt;
    private Long validUntil;

    private Long archivedAt;
    private Long completedAt;

    private Boolean isAcceptedByCustomer = false;
    private Boolean isArchived = false;
    private Boolean isCompleted = false;
}
