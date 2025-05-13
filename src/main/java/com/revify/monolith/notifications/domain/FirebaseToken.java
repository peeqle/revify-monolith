package com.revify.monolith.notifications.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FirebaseToken {
    @Id
    private ObjectId id;
    private Long userId;
    private String registrationId;
    private Long createdAt = Instant.now().toEpochMilli();
}
