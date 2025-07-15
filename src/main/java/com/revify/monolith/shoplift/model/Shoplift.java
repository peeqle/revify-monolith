package com.revify.monolith.shoplift.model;

import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.commons.items.Category;
import com.revify.monolith.geo.model.GeoLocation;
import com.revify.monolith.shoplift.model.req.Create_Shoplift;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("shoplift")
public class Shoplift {
    @Id
    private ObjectId id;
    private String title;
    private String description;

    private List<String> shopIds;

    private Long creatorId;
    private Set<Long> involvedCourierIds;

    private GeoLocation destination;

    private Set<Category> presentCategories;
    private Set<Category> projectedCategories;

    private Set<String> connectedItems;

    private Price minEntryDeliveryPrice;
    private Price EURminEntryDeliveryPrice;

    private Price maxEntryPrice;
    private Integer entries;

    private Long deliveryCutoffTime;
    private Long createdAt;
    private Long updatedAt;

    private Price cumulativePrice;

    private Boolean isRecurrent;
    private Boolean isActive;
    private Boolean allowedSystemAppend;

    public static Shoplift from(Create_Shoplift createShoplift) {
        return Shoplift.builder()
                .shopIds(createShoplift.getShopIds())
                .title(createShoplift.getTitle())
                .description(createShoplift.getDescription())
                .minEntryDeliveryPrice(createShoplift.getMinEntryDeliveryPrice())
                .entries(createShoplift.getMaxEntries())
                .deliveryCutoffTime(createShoplift.getDeliveryCutoffTime())
                .isRecurrent(createShoplift.getIsRecurrent())
                .allowedSystemAppend(createShoplift.getAllowedSystemAppend())
                .build();
    }
}
