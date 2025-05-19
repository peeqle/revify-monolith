package com.revify.monolith.notifications.service;

import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.notifications.models.Notification;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MongoTemplate mongoTemplate;

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void createNotification(Notification notification) {
        Notification savedNotification = mongoTemplate.save(notification);

        Notification.NotificationDTO body =
                Notification.NotificationDTO.from(savedNotification);
        for (Long user : notification.getRelatedUsers()) {
            simpMessagingTemplate.convertAndSend("/topic/notifications/" + user, body);
        }
    }

    public List<Notification> fetchUserNotifications() {
        Query query = Query.query(
                Criteria.where("relatedUsers").in(UserUtils.getUserId())
        ).with(Sort.by(Sort.Direction.DESC, "createdAt"));

        return mongoTemplate.find(query, Notification.class);
    }

    public void deleteNotifications(List<String> notificationIds) {
        Query query = Query.query(
                Criteria.where("_id").in(notificationIds.stream().filter(x -> !ObjectId.isValid(x))
                        .map(ObjectId::new).collect(Collectors.toSet()))
        );
        mongoTemplate.remove(query, Notification.class);
    }
}
