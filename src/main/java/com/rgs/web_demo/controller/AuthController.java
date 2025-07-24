package com.rgs.web_demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rgs.web_demo.dto.LoginRequestDto;
import com.rgs.web_demo.dto.LoginResponseDto;
import com.rgs.web_demo.dto.LogoutRequestDto;
import com.rgs.web_demo.service.RefreshTokenService;
import com.rgs.web_demo.service.TokenBlacklistService;
import com.rgs.web_demo.service.UserService;
import com.rgs.web_demo.util.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService ;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil, RefreshTokenService refreshTokenService, TokenBlacklistService tokenBlacklistService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
        if (!userService.authenticate(request.getUsername(), request.getPassword())) {
            return ResponseEntity.status(401).build();
        }

        String accessToken = jwtUtil.generateAccessToken(request.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(request.getUsername());

        return ResponseEntity.ok(new LoginResponseDto(accessToken, refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequestDto request) {
        String refreshToken = request.getRefreshToken();
        String username = jwtUtil.getUsernameFromToken(refreshToken);

        // 리프레시 토큰 만료시간(Unix timestamp ms)
        long expiration = jwtUtil.getExpirationFromToken(refreshToken); // 만료시간 추출 메서드 필요

        long now = System.currentTimeMillis();
        long expirationMs = expiration - now;
        if (expirationMs < 0) expirationMs = 0; // 이미 만료된 경우 방어

        // 블랙리스트 추가 (만료시간 포함)
        tokenBlacklistService.blacklistToken(refreshToken, expirationMs);

        // 리프레시 토큰 DB/Redis에서 삭제
        refreshTokenService.deleteRefreshToken(username);

        return ResponseEntity.ok("로그아웃 완료");
    }

}
