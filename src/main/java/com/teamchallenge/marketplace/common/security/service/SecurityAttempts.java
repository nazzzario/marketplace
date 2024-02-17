package com.teamchallenge.marketplace.common.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SecurityAttempts {

    private static final String VALUE_ZERO = "0";

    private final RedisTemplate<String, String> redisTemplate;

    public void incrementCounterAttempt(String blockedPrefix, String exceptionPrefix, String key,
                                        int limitation, long timeout){

        redisTemplate.opsForHash().increment(exceptionPrefix, key, 1);

        if (Integer.parseInt(Optional.ofNullable((String) redisTemplate.opsForHash()
                .get(exceptionPrefix, key)).orElse(VALUE_ZERO)) >= limitation) {
            redisTemplate.opsForValue().set(blockedPrefix + key, Boolean.TRUE.toString(),
                    timeout, TimeUnit.MINUTES);
            redisTemplate.opsForHash().delete(exceptionPrefix, key);
        }
    }

    public boolean isAttemptExhausted(String blockedPrefix, String key){
        return Boolean.parseBoolean(redisTemplate.opsForValue().get(blockedPrefix + key));
    }

    public void delete(String exceptionPrefix, String key) {
        redisTemplate.opsForHash().delete(exceptionPrefix, key);
    }

    public boolean isUsedSingleAttempt(String blockedPrefix, String email, long timeout) {
        if (Boolean.parseBoolean(redisTemplate.opsForValue().get(blockedPrefix + email))) {
            return true;
        }

        redisTemplate.opsForValue().set(blockedPrefix + email, Boolean.TRUE.toString(),
                timeout, TimeUnit.MINUTES);

        return false;
    }

    public void setVerificationCode(String key, String code, long timeout) {
        redisTemplate.opsForValue().set(key, code, timeout, TimeUnit.MINUTES);
    }

    public boolean isNotVerificationCode(String key, String code) {
        return !Objects.equals(redisTemplate.opsForValue().get(key), code);
    }

    public void deleteVerificationCode(String key) {
        redisTemplate.delete(key);
    }
}
