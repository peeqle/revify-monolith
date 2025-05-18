package com.revify.monolith.bid;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequiredArgsConstructor
public class BidWsController {
    private ConcurrentHashMap<String, Integer> bidUsers = new ConcurrentHashMap<>();

//    @MessageMapping("/bid")
//    @SendTo("/topic/bids")
//    public OutputMessage handleBid(String message) {
//        ItemBidWSM itemBidWSM = gson.fromJson(message, ItemBidWSM.class);
//        if (itemBidWSM != null) {
//            bidProducer.sendMessage(itemBidWSM);
//            return new OutputMessage(true, SUCCESS.getCode());
//        }
//        return new OutputMessage(false, IS_NULL.getCode());
//    }
}
