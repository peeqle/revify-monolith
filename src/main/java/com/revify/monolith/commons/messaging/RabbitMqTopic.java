package com.revify.monolith.commons.messaging;

public interface RabbitMqTopic {
    String GLOBAL_NOTIFICATIONS = "n_global";
    String ADMIN_NOTIFICATIONS = "n_admin";
    String USER_NOTIFICATIONS = "n_user";

    String EMAIL_NOTIFICATIONS = "n_email";
    String COURIER_NOTIFICATIONS = "n_courier";
}
