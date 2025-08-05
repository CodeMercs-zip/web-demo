package com.rgs.web_demo.service;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;

    // 리프레시 토큰 저장 (username 기준, 만료시간 설정)
    public void saveRefreshToken(String username, String refreshToken, long expirationMs) {
        redisTemplate.opsForValue().set("refresh:" + username, refreshToken, Duration.ofMillis(expirationMs));
    }

    // 리프레시 토큰 검증 (username, token 일치 확인)
    public boolean validateRefreshToken(String username, String refreshToken) {
        String storedToken = redisTemplate.opsForValue().get("refresh:" + username);
        return refreshToken.equals(storedToken);
    }

    // 로그아웃 시 토큰 삭제
    public void deleteRefreshToken(String username) {
        redisTemplate.delete("refresh:" + username);
    }
    
    public boolean exists(String email) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("refresh:" + email));
    }
}
