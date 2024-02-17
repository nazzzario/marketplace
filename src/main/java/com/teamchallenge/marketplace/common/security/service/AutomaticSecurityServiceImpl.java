package com.teamchallenge.marketplace.common.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutomaticSecurityServiceImpl {
    private static final String[] exceptionsPrefix = {"ExceptionVerification_",
            "ExceptionReset_", "ExceptionAuth_"};

    private final RedisTemplate<String, String> redisTemplate;

    @Scheduled(cron = Scheduled.CRON_DISABLED)
    public void resetAttempt() {
        for (String prefix : exceptionsPrefix) {
            redisTemplate.delete(prefix);
        }
    }
}
