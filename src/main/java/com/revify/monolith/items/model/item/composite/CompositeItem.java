package com.revify.monolith.items.model.item.composite;

import com.revify.monolith.commons.geolocation.GeoLocation;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Data
@Document(collection = "composite_items")
public class CompositeItem {
    @Id
    private ObjectId id;

    private Long creatorId;
    private Set<Long> usersConnected = new HashSet<>();
    private String initialItemId;

    //try not to aggregate on coords when composing
    private GeoLocation destination;

    private Set<String> itemsInvolved = new HashSet<>();
    private Set<String> itemsCategories = new HashSet<>();

    private Boolean isAvailableForAppend = true;
    private Boolean isActive = true;

    public CompositeItem addUserConnected(Long userId) {
        usersConnected.add(userId);
        return this;
    }
}
