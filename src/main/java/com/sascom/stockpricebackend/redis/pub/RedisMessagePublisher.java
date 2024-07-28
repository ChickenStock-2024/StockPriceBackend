package com.sascom.stockpricebackend.redis.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisMessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(String destination, String message) {
        redisTemplate.convertAndSend(destination, message);
    }
}
