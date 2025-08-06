package com.rgs.web_demo.service;

import com.rgs.web_demo.domain.Member;
import com.rgs.web_demo.dto.LoginRequestDto;
import com.rgs.web_demo.dto.LoginResponseDto;
import com.rgs.web_demo.dto.LogoutRequestDto;
import com.rgs.web_demo.dto.request.MemberCreateRequestDto;
import com.rgs.web_demo.dto.response.ApiResponseDto;
import com.rgs.web_demo.dto.response.MemberResponseDto;
import com.rgs.web_demo.repository.MemberNormalRepository;
import com.rgs.web_demo.util.FormatUtil;
import com.rgs.web_demo.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberNormalRepository memberRepository;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<ApiResponseDto<MemberResponseDto>> signup(MemberCreateRequestDto requestDto) {
        if (memberRepository.existsByEmail(requestDto.getEmail())) {
            return ResponseEntity.status(409)
                    .body(ApiResponseDto.of("이미 가입된 이메일입니다."));
        }

        Member member = Member.builder()
                .name(requestDto.getName())
                .phoneNumber(FormatUtil.numberFormat(requestDto.getPhoneNumber()))
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .memberType(requestDto.getMemberType())
                .isDeleted(false)
                .build();

        Member saved = memberRepository.save(member);

        return ResponseEntity.status(201)
                .body(ApiResponseDto.of("회원가입이 완료되었습니다.", MemberResponseDto.from(saved)));
    }

    public ResponseEntity<ApiResponseDto<LoginResponseDto>> login(LoginRequestDto request) {
        Optional<Member> optionalMember = memberRepository.findByEmail(request.getEmail());

        if (optionalMember.isEmpty() ||
            !passwordEncoder.matches(request.getPassword(), optionalMember.get().getPassword())) {
            return ResponseEntity.status(401)
                    .body(ApiResponseDto.of("이메일 또는 비밀번호가 올바르지 않습니다."));
        }

        Member member = optionalMember.get();

        String accessToken = jwtUtil.generateAccessToken(member.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(member.getEmail());
        long expirationMs = jwtUtil.getExpirationFromToken(refreshToken) - System.currentTimeMillis();

//         try {
//             refreshTokenService.saveRefreshToken(member.getEmail(), refreshToken, expirationMs);
//         } catch (Exception e) {
//             log.error("Redis에 refreshToken 저장 실패: {}", e.getMessage());
//             return ResponseEntity.status(500)
//                     .body(ApiResponseDto.of("로그인 중 오류가 발생했습니다."));
//         }

        return ResponseEntity.ok(
                ApiResponseDto.of("로그인 성공", new LoginResponseDto(accessToken, refreshToken))
        );
    }

    public ResponseEntity<ApiResponseDto<Void>> logout(HttpServletRequest request, LogoutRequestDto logoutRequestDto) {
        String accessToken = jwtUtil.resolveToken(request);
        String refreshToken = logoutRequestDto.getRefreshToken();

        if (accessToken != null && jwtUtil.validateToken(accessToken)) {
            long accessExp = jwtUtil.getExpirationFromToken(accessToken) - System.currentTimeMillis();
            tokenBlacklistService.blacklistToken(accessToken, accessExp);
        }

        if (refreshToken != null && jwtUtil.validateToken(refreshToken)) {
            String email = jwtUtil.getUserIdFromToken(refreshToken);
            long refreshExp = jwtUtil.getExpirationFromToken(refreshToken) - System.currentTimeMillis();

            tokenBlacklistService.blacklistToken(refreshToken, refreshExp);
            refreshTokenService.deleteRefreshToken(email);
        }

        return ResponseEntity.ok(ApiResponseDto.of("로그아웃 완료"));
    }
}
