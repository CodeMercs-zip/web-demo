package com.rgs.web_demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenBlacklistService {

    private final StringRedisTemplate redisTemplate;

    // 토큰 블랙리스트 추가 (만료시간 설정!)
    public void blacklistToken(String token, long expirationMs) {
        redisTemplate.opsForValue().set("blacklist:" + token, "blacklisted", Duration.ofMillis(expirationMs));
    }

    // 블랙리스트 확인
    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey("blacklist:" + token);
    }
}
