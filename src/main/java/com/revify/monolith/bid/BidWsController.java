package com.revify.monolith.bid;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class BidWsController {


//    @MessageMapping("/bids/{itemId}/send")
//    public void sendItemMessage(
//            @DestinationVariable String itemId,
//            String message,
//            @AuthenticationPrincipal User user) {
//
//        System.out.println("Received message for item " + itemId + " from " + user.getUsername());
//
//        // Send to item-specific topic
//        messagingTemplate.convertAndSend("/topic/items/" + itemId, message);
//
//        // You could also send to RabbitMQ here if needed
//    }
}
