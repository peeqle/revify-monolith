package com.revify.monolith.resource.data.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("entity_file")
public class EntityFile {

    private ObjectId id;
    private String entityId;
    private String customFileId;

    public EntityFile(String entityId, String customFileId) {
        this.entityId = entityId;
        this.customFileId = customFileId;
    }
}
