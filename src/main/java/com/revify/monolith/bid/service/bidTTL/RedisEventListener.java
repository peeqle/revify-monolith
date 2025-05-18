package com.revify.monolith.bid.service.bidTTL;

import org.springframework.data.redis.connection.Message;

@FunctionalInterface
public interface RedisEventListener {
    String onMessage(Message message);
}
