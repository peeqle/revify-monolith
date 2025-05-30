package com.revify.monolith.orders.models;

import com.revify.monolith.commons.models.orders.OrderShipmentParticle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PathSegmentDTO {
    private String id;
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

    public static PathSegmentDTO from(PathSegment pathSegment) {
        return PathSegmentDTO.builder()
                .id(pathSegment.getId().toHexString())
                .particle(pathSegment.getParticle())
                .courierId(pathSegment.getCourierId())
                .acceptedCourierId(pathSegment.getAcceptedCourierId())
                .createdAt(pathSegment.getCreatedAt())
                .validUntil(pathSegment.getValidUntil())
                .completedAt(pathSegment.getCompletedAt())
                .archivedAt(pathSegment.getArchivedAt())
                .isAcceptedByCustomer(pathSegment.getIsAcceptedByCustomer())
                .isArchived(pathSegment.getIsArchived())
                .isCompleted(pathSegment.getIsCompleted())
                .build();
    }
}
