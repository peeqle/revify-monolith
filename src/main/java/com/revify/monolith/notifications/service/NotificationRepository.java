package com.revify.monolith.notifications.service;

import com.revify.monolith.notifications.models.Notification;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    @Query(value = "{'recipientId': ?0}", sort = "{'createdAt': -1}")
    Page<Notification> findByRecipientId(Long userId, Pageable pageable);

    @Query(value = "{'recipientId': ?0, 'isRead': false}", count = true)
    long countUnreadByRecipientId(Long userId);

    @Update("{'$addToSet': {'readByUserIds': ?1}, '$inc': {'readCount': 1}, '$set': {'lastReadTimestamp': ?2, 'isRead': true}}")
    void markAsRead(ObjectId notificationId, Long userId, Long timestamp);

    @Query(value = "{'recipientId': ?0, 'createdAt': {$gt: ?1}}", sort = "{'createdAt': -1}")
    List<Notification> findRecentByRecipientId(Long userId, Instant since);
}
