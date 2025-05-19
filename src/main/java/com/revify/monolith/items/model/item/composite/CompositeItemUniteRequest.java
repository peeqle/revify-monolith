package com.revify.monolith.items.model.item.composite;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "composite_item_unite_request")
public class CompositeItemUniteRequest {
    @Id
    private ObjectId id;

    private String compositeItemId;
    //item to unite
    private String itemId;
}
