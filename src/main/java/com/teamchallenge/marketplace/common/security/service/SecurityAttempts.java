package com.teamchallenge.marketplace.common.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SecurityAttempts {

    private static final String VALUE_ZERO = "0";

    private final RedisTemplate<String, String> redisTemplate;

    public boolean attemptExhausted(String blockedPrefix, String exceptionPrefix, String key,
                                    int limitation, long timeout){
        if (Boolean.parseBoolean(redisTemplate.opsForValue().get(blockedPrefix))) {
            return true;
        }

        redisTemplate.opsForHash().increment(exceptionPrefix, key, 1);

        if (Integer.parseInt(Optional.ofNullable((String) redisTemplate.opsForHash().get(exceptionPrefix,
                key)).orElse(VALUE_ZERO)) >= limitation) {
            redisTemplate.opsForValue().set(blockedPrefix, Boolean.TRUE.toString(), timeout, TimeUnit.MINUTES);
            redisTemplate.opsForHash().delete(exceptionPrefix, key);
        }

        return false;
    }
}
