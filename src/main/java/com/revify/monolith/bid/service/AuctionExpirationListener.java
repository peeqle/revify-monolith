package com.revify.monolith.bid.service;

import com.revify.monolith.bid.service.bidTTL.RedisEventListener;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionExpirationListener implements RedisEventListener {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public String onMessage(Message message) {
        return new String(message.getBody());
    }
}
