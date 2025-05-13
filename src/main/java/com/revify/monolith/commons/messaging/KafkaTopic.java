package com.revify.monolith.commons.messaging;

public interface KafkaTopic {
    String USER_CONTEXT_ADD = "user_context_add";
    String USER_GEOLOCATION_SYNC = "user_geolocation_sync";
    String RECIPIENT_CREATION = "recipient_create";

    String BID_TUNNEL = "bids_item";
    String AUCTION_CREATION = "auction_create";
    String AUCTION_DEACTIVATION = "auction_deactivation";
    String AUCTION_CHANGES = "auction_changes";
    String AUCTION_RECREATION_EXPLICIT = "auction_recreation_explicit";

    String ORDER_MODEL_CREATION = "order_model_create";
    String ORDER_STATUS_UPDATE = "order_status_update";

    String USER_NOTIFICATIONS = "user_notifications";
    String MODERATOR_NOTIFICATIONS = "moderator_notifications";

    String BILLING_CREATION = "billing_create";
    String BILLING_UPDATES = "billing_updates";
    String PROCESS_PAYMENT = "process_payment";
    String INSURANCE_ITEM_CREATE = "insurance_item_create";

    String ITEM_STATUS_TRACKER = "item_status";
    String ITEM_PROCESSING_MODEL = "item_processing_model";
    String ITEM_PROCESSING_RESPONSE = "item_processing_response";

    String MESSAGES_CHANNEL = "messages_channel";
    String ROOM_CREATION = "room_create";

    String CHAT_REGISTRATION = "chat_registration";
}
