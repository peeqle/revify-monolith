package com.revify.monolith;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RateLimitService {
    private final RedisTemplate<String, String> redisTemplate;

    public boolean isRateLimited(String key, int limit, long durationSeconds) {
        Long currentCount = redisTemplate.opsForValue().increment(key);
        if (currentCount == 1) {
            redisTemplate.expire(key, durationSeconds, TimeUnit.SECONDS);
        }
        return currentCount > limit;
    }

    public void recordRequest(String key) {
        redisTemplate.opsForValue().increment(key);
    }
}
