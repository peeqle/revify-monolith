package com.revify.monolith.commons.messaging;

import lombok.Getter;

@Getter
public enum DirectNotificationTopic {
    BID("notification_bids"),
    ITEM_CLIENT_SUBSCRIPTION("notification_client_subscription");

    private final String notificationTopicName;

    DirectNotificationTopic(String name) {
        this.notificationTopicName = name;
    }
}
