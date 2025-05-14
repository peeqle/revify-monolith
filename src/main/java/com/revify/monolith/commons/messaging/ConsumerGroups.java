package com.revify.monolith.commons.messaging;

public interface ConsumerGroups {
    String SYSTEM = "system";
    String USER = "user";
    String BIDS = "bids";
    String BIDS_HIGHEST = "bids_highest";
    String ORDERS = "orders";

    String RECIPIENTS = "recipients";
    String INSURANCES = "insurances";

    String AUCTION_STATUS = "auction_status";

    String CHAT = "chat_data";

    String ITEM_PROCESSING_REQUEST = "item_processing_request";
    String ITEM_PROCESSING_RESPONSE = "item_processing_response";
}
