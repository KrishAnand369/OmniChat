package com.krish.chatApp.service.impl;

import com.krish.chatApp.service.RateLimiterService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimiterServiceImpl implements RateLimiterService {

    private final StringRedisTemplate redisTemplate;

    // Config: Allow 50 messages per second per tenant
    private static final int MAX_REQUESTS_PER_SECOND = 50;

    public RateLimiterServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean isAllowed(String tenantId) {
        // 1. Generate a key specific to this Tenant + This Second
        // Key example: "rate:food_app:1698230400"
        long currentSecond = Instant.now().getEpochSecond();
        String key = "rate:" + tenantId + ":" + currentSecond;

        // 2. Increment the counter atomically
        Long requests = redisTemplate.opsForValue().increment(key);

        // 3. Set expiration (Save memory)
        // We only need this key for 2 seconds (buffer), then delete it.
        if (requests == 1) {
            redisTemplate.expire(key, 5, TimeUnit.SECONDS);
        }

        // 4. Check limit
        return requests <= MAX_REQUESTS_PER_SECOND;
    }
}