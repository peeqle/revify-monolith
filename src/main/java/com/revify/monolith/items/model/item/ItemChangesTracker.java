package com.revify.monolith.items.model.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Document("item_changes")

@RequiredArgsConstructor
@AllArgsConstructor
public class ItemChangesTracker {
    private ObjectId itemId;
    private Long timestamp;

    private List<String> changedFields = new ArrayList<>();
    private Map<String, Object> oldValues = new HashMap<>();
    private Map<String, Object> newValues = new HashMap<>();

    public void addChange(String fieldName, Object oldValue, Object newValue) {
        changedFields.add(fieldName);
        oldValues.put(fieldName, oldValue);
        newValues.put(fieldName, newValue);
    }
}
