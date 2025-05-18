package com.revify.monolith.notifications.service.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.revify.monolith.notifications.domain.FirebaseToken;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FcmService {

    private final FcmTokenService fcmTokenService;

    public void sendTopic(String topic, @NonNull String title, @NonNull String body) {
        Message message = Message.builder()
                .setTopic(topic)
                .setNotification(
                        Notification.builder()
                                .setBody(body)
                                .setTitle(title)
                                .build()
                )
                .build();

        sendMessage(message);
    }

    public void sendDirect(@NonNull Long userId, @NonNull String title, @NonNull String body) {
        FirebaseToken firebaseToken = fcmTokenService.findTokenByUserId(userId);

        if (firebaseToken != null) {
            Message message = Message.builder()
                    .setToken(firebaseToken.getRegistrationId())
                    .setNotification(
                            Notification.builder()
                                    .setBody(body)
                                    .setTitle(title)
                                    .build()
                    )
                    .build();

            sendMessage(message);
        }
    }

    public void sendTopicExcluding(List<Long> userIds, String topic, String title, String body) {
        List<FirebaseToken> tokens = fcmTokenService.findTokens(userIds);
        if (tokens != null) {
            for (FirebaseToken token : tokens) {
                Message message = Message.builder()
                        .setTopic(topic)
                        .setToken(token.getRegistrationId())
                        .setNotification(
                                Notification.builder()
                                        .setBody(body)
                                        .setTitle(title)
                                        .build()
                        )
                        .build();
                sendMessage(message);
            }
        }
    }

    public void sendDirectToTopic(String topic, @NonNull Long userId, @NonNull String title, @NonNull String body) {
        FirebaseToken firebaseToken = fcmTokenService.findTokenByUserId(userId);
        if (firebaseToken != null) {
            Message message = Message.builder()
                    .setTopic(topic)
                    .setToken(firebaseToken.getRegistrationId())
                    .setNotification(
                            Notification.builder()
                                    .setBody(body)
                                    .setTitle(title)
                                    .build()
                    )
                    .build();

            sendMessage(message);
        }
    }

    public static void sendMessage(@NonNull Message message) {
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
