package com.revify.monolith.items.model.item;

import com.revify.monolith.commons.finance.Price;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "items")
public class Item implements Serializable {
    @Id
    private ObjectId id;

    private Long creatorId;

    private ItemDescription itemDescription = new ItemDescription();

    private List<String> referenceUrl = new ArrayList<>();

    private Price price;

    private Long createdAt;
    private Long updatedAt;

    private Long validUntil;

    private boolean isPicked = false;
    private boolean isActive = true;
    private boolean isManuallyToggled = false;

    private String shopliftId;
}
