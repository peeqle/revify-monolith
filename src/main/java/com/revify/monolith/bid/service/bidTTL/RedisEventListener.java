package com.revify.monolith.bid.service.bidTTL;

@FunctionalInterface
public interface RedisEventListener {

    String onMessage(String message);
}
