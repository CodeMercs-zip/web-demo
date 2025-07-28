package com.rgs.web_demo.service;

import com.rgs.web_demo.domain.Member;
import com.rgs.web_demo.dto.oauth.OAuth2UserInfo;
import com.rgs.web_demo.dto.oauth.OAuth2UserInfoFactory;
import com.rgs.web_demo.enumeration.MemberType;
import com.rgs.web_demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        OAuth2UserInfo oauth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oauth2User.getAttributes());

        if (oauth2UserInfo.getEmail() == null || oauth2UserInfo.getEmail().isEmpty()) {
            throw new OAuth2AuthenticationException("OAuth2 인증 과정에서 이메일을 찾을 수 없습니다.");
        }

        getOrCreateMember(oauth2UserInfo);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                oauth2User.getAttributes(),
                userNameAttributeName
        );
    }

    private Member getOrCreateMember(OAuth2UserInfo oauth2UserInfo) {
        String providerId = oauth2UserInfo.getProvider() + "_" + oauth2UserInfo.getId();

        Optional<Member> existingMember = memberRepository.findByProviderId(providerId);

        if (existingMember.isPresent()) {
            log.info("기존 소셜 로그인 사용자: {}", oauth2UserInfo.getEmail());
            return existingMember.get();
        }

        // 이메일로 기존 회원 확인
        Optional<Member> memberByEmail = memberRepository.findByEmail(oauth2UserInfo.getEmail());
        if (memberByEmail.isPresent()) {
            // 기존 회원에 소셜 로그인 정보 연결
            Member member = memberByEmail.get();
            member.linkSocialAccount(providerId, oauth2UserInfo.getProvider());
            return memberRepository.save(member);
        }

        // 새 회원 생성
        Member newMember = Member.builder()
                .name(oauth2UserInfo.getName())
                .email(oauth2UserInfo.getEmail())
                .password("OAUTH2_USER") // OAuth2 사용자는 비밀번호 불필요
                .memberType(MemberType.USER)
                .providerId(providerId)
                .provider(oauth2UserInfo.getProvider())
                .build();

        log.info("새로운 소셜 로그인 사용자 생성: {}", oauth2UserInfo.getEmail());
        return memberRepository.save(newMember);
    }
}