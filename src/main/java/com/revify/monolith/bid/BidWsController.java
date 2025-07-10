package com.revify.monolith.bid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BidWsController {

    @MessageMapping("/subscribe/item/bids/{itemId}")
    @SendTo("/topic/bids/updates/{itemId}")
    public String subscribeBidsUpdates(@PathVariable String itemId) {
        log.debug("Client subscribed to item updates for item ID: " + itemId);
        return "Subscribed to item updates for item ID: " + itemId;
    }
}
