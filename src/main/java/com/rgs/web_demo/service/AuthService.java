package com.rgs.web_demo.service;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.rgs.web_demo.dto.LoginRequestDto;
import com.rgs.web_demo.dto.LoginResponseDto;
import com.rgs.web_demo.dto.LogoutRequestDto;
import com.rgs.web_demo.dto.request.MemberCreateRequestDto;
import com.rgs.web_demo.dto.response.ApiResponseDto;
import com.rgs.web_demo.dto.response.MemberResponseDto;
import com.rgs.web_demo.mapper.MemberMapper;
import com.rgs.web_demo.util.FormatUtil;
import com.rgs.web_demo.util.JwtUtil;
import com.rgs.web_demo.vo.MemberVo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberMapper memberMapper;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<ApiResponseDto<MemberResponseDto>> signup(MemberCreateRequestDto requestDto) {
        String rawPhone = requestDto.getPhoneNumber();

        String formattedPhone = FormatUtil.numberFormat(rawPhone.replaceAll("[^\\d]", ""));

        MemberVo newMember = new MemberVo();
        newMember.setName(requestDto.getName());
        newMember.setEmail(requestDto.getEmail());
        newMember.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        newMember.setPhoneNumber(formattedPhone); // 포맷 적용된 번호
        newMember.setMemberType(requestDto.getMemberType());
        newMember.setMemberUuid(UUID.randomUUID().toString());

        int result = memberMapper.insertMember(newMember);
        if (result == 0) {
            return ResponseEntity.status(500)
                    .body(ApiResponseDto.of("회원가입에 실패했습니다."));
        }

        return ResponseEntity.status(201)
                .body(ApiResponseDto.of("회원가입이 완료되었습니다.", MemberResponseDto.from(newMember)));
    }

    public boolean authenticate(String email, String password) {
        MemberVo member = memberMapper.selectMemberByEmail(email);
        if (member == null) {
            log.warn("로그인 실패 - 존재하지 않는 이메일: {}", email);
            return false;
        }

        return passwordEncoder.matches(password, member.getPassword());
    }

    public ResponseEntity<ApiResponseDto<LoginResponseDto>> login(LoginRequestDto request) {
        String email = request.getEmail();

        if (!authenticate(email, request.getPassword())) {
            return ResponseEntity.status(401)
                    .body(ApiResponseDto.of("이메일 또는 비밀번호가 올바르지 않습니다."));
        }

        log.info("PasswordEncoder 구현체: {}", passwordEncoder.getClass().getName());

        // 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(email);
        String refreshToken = jwtUtil.generateRefreshToken(email);
        long expirationMs = jwtUtil.getExpirationFromToken(refreshToken) - System.currentTimeMillis();

        // Redis 저장 실패 시 로그인 실패로 처리
        try {
            refreshTokenService.saveRefreshToken(email, refreshToken, expirationMs);
        } catch (Exception e) {
            log.error("Redis에 refreshToken 저장 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDto.of("로그인 중 오류가 발생했습니다. 관리자에게 문의하세요."));
        }

        return ResponseEntity.ok(ApiResponseDto.of("로그인 성공", new LoginResponseDto(accessToken, refreshToken)));
    }

    public ResponseEntity<ApiResponseDto<Void>> logout(HttpServletRequest request, @RequestBody LogoutRequestDto logoutRequestDto) {
        String accessToken = jwtUtil.resolveToken(request);
        String refreshToken = logoutRequestDto.getRefreshToken();

        // accessToken 블랙리스트 처리
        if (accessToken != null && jwtUtil.validateToken(accessToken)) {
            long accessExp = jwtUtil.getExpirationFromToken(accessToken) - System.currentTimeMillis();
            tokenBlacklistService.blacklistToken(accessToken, accessExp);
        }

        // refreshToken 삭제 및 블랙리스트 처리
        if (refreshToken != null && jwtUtil.validateToken(refreshToken)) {
            String email = jwtUtil.getUserIdFromToken(refreshToken);
            long refreshExp = jwtUtil.getExpirationFromToken(refreshToken) - System.currentTimeMillis();

            tokenBlacklistService.blacklistToken(refreshToken, refreshExp);
            refreshTokenService.deleteRefreshToken(email);
        }

        return ResponseEntity.ok(ApiResponseDto.of("로그아웃 완료"));
    }
}
