package com.revify.monolith.items.model.item.composite;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Data
@Document(value = "composite_item_request_link")

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompositeItemRequestLink {
    @Id
    private ObjectId id;
    private String hashKey;

    @DocumentReference
    @JsonIgnore
    private CompositeItem compositeItem;

    private Long createdAt = Instant.now().toEpochMilli();
    @Indexed(expireAfterSeconds = 0)
    private Long validUntil = Instant.now().plus(3, ChronoUnit.DAYS).toEpochMilli();

    private Boolean isActive = true;

    public record CompositeItemRequestLinkDTO(String hash, Long validUntil, Boolean isActive) {
        public static CompositeItemRequestLinkDTO from(CompositeItemRequestLink link) {
            return new CompositeItemRequestLinkDTO(link.getHashKey(), link.getValidUntil(), link.getIsActive());
        }
    }
}
