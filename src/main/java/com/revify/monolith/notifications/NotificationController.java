package com.revify.monolith.notifications;

import com.revify.monolith.notifications.models.Notification;
import com.revify.monolith.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Notification.NotificationDTO>> getCurrentUserNotifications() {
        return ResponseEntity.ok(notificationService.fetchUserNotifications()
                .stream().map(Notification.NotificationDTO::from)
                .collect(Collectors.toList()));
    }

    @DeleteMapping
    public void deleteNotifications(@RequestParam("notificationIds") List<String> notificationIds) {
        notificationService.deleteNotifications(notificationIds);
    }
}
