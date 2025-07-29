package com.rgs.web_demo.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rgs.web_demo.dto.LoginRequestDto;
import com.rgs.web_demo.dto.LoginResponseDto;
import com.rgs.web_demo.dto.LogoutRequestDto;
import com.rgs.web_demo.dto.request.MemberCreateRequestDto;
import com.rgs.web_demo.dto.response.ApiResponseDto;
import com.rgs.web_demo.dto.response.MemberResponseDto;
import com.rgs.web_demo.mapper.MemberMapper;
import com.rgs.web_demo.util.JwtUtil;
import com.rgs.web_demo.vo.MemberVo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberMapper memberMapper;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    public ResponseEntity<ApiResponseDto<MemberResponseDto>> signup(MemberCreateRequestDto requestDto) {
        if (memberMapper.selectMemberByEmail(requestDto.getEmail()) != null) {
            return ResponseEntity.status(400)
                    .body(ApiResponseDto.error("이미 가입된 이메일입니다."));
        }
        
        System.out.println("🧩 Redis Host: " + redisHost);
        System.out.println("🧩 Redis Port: " + redisPort);
        
        MemberVo newMember = new MemberVo();
        newMember.setName(requestDto.getName());
        newMember.setEmail(requestDto.getEmail());
        newMember.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        newMember.setPhoneNumber(requestDto.getPhoneNumber());
        newMember.setMemberType(requestDto.getMemberType());
        newMember.setMemberUuid(UUID.randomUUID().toString());

        int result = memberMapper.insertMember(newMember);
        if (result == 0) {
            return ResponseEntity.status(500)
                    .body(ApiResponseDto.error("회원가입에 실패했습니다."));
        }

        return ResponseEntity.status(201)
                .body(ApiResponseDto.success("회원가입이 완료되었습니다.", MemberResponseDto.from(newMember)));
    }

    public boolean authenticate(String email, String password) {
        MemberVo member = memberMapper.selectMemberByEmail(email);
        return member != null && passwordEncoder.matches(password, member.getPassword());
    }

    public ResponseEntity<ApiResponseDto<LoginResponseDto>> login(LoginRequestDto request) {
        if (!authenticate(request.getEmail(), request.getPassword())) {
            return ResponseEntity.status(401).body(ApiResponseDto.error("이메일 또는 비밀번호가 올바르지 않습니다."));
        }

        String accessToken = jwtUtil.generateAccessToken(request.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(request.getEmail());
        long expirationMs = jwtUtil.getExpirationFromToken(refreshToken) - System.currentTimeMillis();

        refreshTokenService.saveRefreshToken(request.getEmail(), refreshToken, expirationMs);

        return ResponseEntity.ok(ApiResponseDto.success("로그인 성공", new LoginResponseDto(accessToken, refreshToken)));
    }

    public ResponseEntity<ApiResponseDto<Void>> logout(LogoutRequestDto request) {
        String refreshToken = request.getRefreshToken();
        String username = jwtUtil.getUserIdFromToken(refreshToken);
        long expirationMs = Math.max(0, jwtUtil.getExpirationFromToken(refreshToken) - System.currentTimeMillis());

        tokenBlacklistService.blacklistToken(refreshToken, expirationMs);
        refreshTokenService.deleteRefreshToken(username);

        return ResponseEntity.ok(ApiResponseDto.success("로그아웃 완료", null));
    }
}
