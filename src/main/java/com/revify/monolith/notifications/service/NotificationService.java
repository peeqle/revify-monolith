package com.revify.monolith.notifications.service;

import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.notifications.models.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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
                )
                .with(Sort.by(Sort.Direction.DESC, "createdAt"));

        return mongoTemplate.find(query, Notification.class);
    }

    public void deleteNotifications(List<String> notificationIds) {
        long currentUserId = UserUtils.getUserId();
        for (String notificationId : notificationIds) {
            Notification notification = mongoTemplate.findById(notificationId, Notification.class);
            if (notification == null) {
                continue;
            }
            Set<Long> relatedUsers = notification.getRelatedUsers();
            if (relatedUsers != null) {
                relatedUsers.removeIf(x -> x == currentUserId);
            }

            if (relatedUsers == null || relatedUsers.isEmpty()) {
                mongoTemplate.remove(notification);
            } else {
                notification.setRelatedUsers(relatedUsers);
                mongoTemplate.save(notification);
            }
        }
    }
}
