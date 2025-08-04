package com.rgs.web_demo.service;

import com.rgs.web_demo.domain.Member;
import com.rgs.web_demo.dto.oauth.OAuth2UserInfo;
import com.rgs.web_demo.dto.request.*;
import com.rgs.web_demo.dto.response.AuthResponseDto;
import com.rgs.web_demo.dto.response.MemberResponseDto;
import com.rgs.web_demo.exception.BusinessException;
import com.rgs.web_demo.exception.MemberErrorCodes;
import com.rgs.web_demo.repository.MemberRepository;
import com.rgs.web_demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final SocialAuthService socialAuthService;
    private final JwtUtil jwtUtil;

    /**
     * 일반 회원가입 (JWT 토큰 발급)
     */
    @Transactional
    public AuthResponseDto signup(MemberCreateRequestDto requestDto) {
        // 이메일 중복 검증
        if (memberRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new BusinessException(MemberErrorCodes.ALREADY_EXISTS_EMAIL);
        }

        // 새 회원 생성
        Member member = Member.builder()
                .name(requestDto.getName())
                .phoneNumber(requestDto.getPhoneNumber())
                .email(requestDto.getEmail())
                .password(requestDto.getPassword()) // TODO: 암호화 필요
                .memberType(requestDto.getMemberType())
                .build();

        Member savedMember = memberRepository.save(member);
        return generateAuthResponse(savedMember);
    }

    /**
     * 일반 로그인 (이메일/패스워드)
     */
    public AuthResponseDto login(LoginRequestDto requestDto) {
        // 이메일로 회원 조회
        Member member = memberRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new BusinessException(MemberErrorCodes.INVALID_LOGIN_CREDENTIALS));

        // 패스워드 검증 (현재는 평문 비교, TODO: 암호화된 패스워드 비교 필요)
        if (!member.getPassword().equals(requestDto.getPassword())) {
            throw new BusinessException(MemberErrorCodes.INVALID_LOGIN_CREDENTIALS);
        }

        return generateAuthResponse(member);
    }

    /**
     * 회원 단일 조회
     */
    public MemberResponseDto getMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(MemberErrorCodes.NOT_FOUND_MEMBER));
        return MemberResponseDto.from(member);
    }

    /**
     * 회원 목록 조회 (페이징)
     */
    public Page<MemberResponseDto> getMembers(Pageable pageable) {
        Page<Member> members = memberRepository.findAll(pageable);
        return members.map(MemberResponseDto::from);
    }

    /**
     * 회원 수정
     */
    @Transactional
    public MemberResponseDto updateMember(Long id, MemberUpdateRequestDto requestDto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(MemberErrorCodes.NOT_FOUND_MEMBER));

        member.updateInfo(
                requestDto.getName() != null ? requestDto.getName() : member.getName(),
                requestDto.getPhoneNumber() != null ? requestDto.getPhoneNumber() : member.getPhoneNumber(),
                requestDto.getEmail() != null ? requestDto.getEmail() : member.getEmail()
        );

        if (requestDto.getMemberType() != null) {
            member.updateMemberType(requestDto.getMemberType());
        }

        return MemberResponseDto.from(member);
    }

    /**
     * 회원 삭제
     */
    @Transactional
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new BusinessException(MemberErrorCodes.NOT_FOUND_MEMBER);
        }
        memberRepository.deleteById(id);
    }

    /**
     * 소셜 로그인
     */
    public AuthResponseDto socialLogin(SocialLoginRequestDto requestDto) {
        // Access Token으로 소셜 Provider 사용자 정보 조회
        OAuth2UserInfo userInfo = socialAuthService.getUserInfo(requestDto.getAccessToken(), requestDto.getProvider());
        // Provider와 소셜 ID를 조합한 고유 식별자 생성
        String providerId = requestDto.getProvider() + "_" + userInfo.getId();

        // 기존 가입된 소셜 회원 조회
        Member member = memberRepository.findByProviderId(providerId)
                .orElseThrow(() -> new BusinessException(MemberErrorCodes.NOT_FOUND_SOCIAL_MEMBER));

        return generateAuthResponse(member);
    }

    /**
     * 소셜 회원가입
     */
    @Transactional
    public AuthResponseDto socialSignup(SocialSignupRequestDto requestDto) {
        // Access Token으로 소셜 Provider 사용자 정보 조회
        OAuth2UserInfo userInfo = socialAuthService.getUserInfo(requestDto.getAccessToken(), requestDto.getProvider());
        // Provider와 소셜 ID를 조합한 고유 식별자 생성
        String providerId = requestDto.getProvider() + "_" + userInfo.getId();

        // 중복 가입 검증 (같은 providerId 또는 이메일로 이미 가입된 경우)
        validateSocialSignup(providerId, userInfo.getEmail());

        // 소셜 로그인 사용자 정보로 새 회원 생성
        Member newMember = Member.builder()
                .name(userInfo.getName())
                .email(userInfo.getEmail())
                .phoneNumber(requestDto.getPhoneNumber())
                .password("SOCIAL_USER") // 소셜 로그인 사용자는 패스워드 불필요
                .memberType(requestDto.getMemberType())
                .providerId(providerId)
                .provider(requestDto.getProvider())
                .build();

        Member savedMember = memberRepository.save(newMember);
        return generateAuthResponse(savedMember);
    }

    private void validateSocialSignup(String providerId, String email) {
        if (memberRepository.findByProviderId(providerId).isPresent()) {
            throw new BusinessException(MemberErrorCodes.ALREADY_EXISTS_SOCIAL_MEMBER);
        }
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new BusinessException(MemberErrorCodes.ALREADY_EXISTS_EMAIL);
        }
    }

    private AuthResponseDto generateAuthResponse(Member member) {
        // JWT Access Token과 Refresh Token 생성
        String accessToken = jwtUtil.generateAccessToken(member.getId().toString());
        String refreshToken = jwtUtil.generateRefreshToken(member.getId().toString());

        return new AuthResponseDto(accessToken, refreshToken, MemberResponseDto.from(member));
    }
}