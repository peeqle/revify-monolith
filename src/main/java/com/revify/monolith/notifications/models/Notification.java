package com.revify.monolith.notifications.models;

import com.revify.monolith.commons.auth.sync.UserUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Document(value = "notification")

@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Notification {
    @Id
    private ObjectId id;

    private String title;
    private String body;

    @Indexed
    private Set<Long> relatedUsers = new HashSet<>();
    private Map<Long, Long> readByUserIdsWithTimestamps = new HashMap<>();

    private String relatedItemId;
    private String relatedCompositeItemId;
    private Long relatedUserId;

    private NotificationType type;

    private Long createdAt = Instant.now().toEpochMilli();
    private Long expiresAt = Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli();

    public record NotificationDTO(String id, String title, String body, String relatedItemId,
                                  String relatedCompositeItemId, Long relatedUserId, NotificationType type,
                                  Long createdAt, Boolean isRead) {

        public static NotificationDTO from(Notification notification) {
            return new NotificationDTO(notification.getId().toHexString(), notification.getTitle(),
                    notification.getBody(), notification.getRelatedItemId(),
                    notification.getRelatedCompositeItemId(), notification.getRelatedUserId(), notification.getType(),
                    notification.getCreatedAt(), notification.getReadByUserIdsWithTimestamps().containsKey(UserUtils.getUserId()));
        }
    }
}
