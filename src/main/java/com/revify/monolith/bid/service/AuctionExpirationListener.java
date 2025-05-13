package com.revify.monolith.bid.service;

import com.revify.monolith.bid.service.bidTTL.RedisEventListener;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionExpirationListener implements RedisEventListener {

    @Autowired
    @Qualifier("bidServiceRedisTemplate")
    private ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    @Override
    public String onMessage(String message) {
        return "";
    }
}
