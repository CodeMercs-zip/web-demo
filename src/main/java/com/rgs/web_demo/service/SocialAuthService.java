package com.rgs.web_demo.service;

import com.rgs.web_demo.dto.oauth.OAuth2UserInfo;
import com.rgs.web_demo.dto.oauth.OAuth2UserInfoFactory;
import com.rgs.web_demo.exception.BusinessException;
import com.rgs.web_demo.exception.MemberErrorCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialAuthService {

    private final RestTemplate restTemplate = new RestTemplate();

    public OAuth2UserInfo getUserInfo(String accessToken, String provider) {
        try {
            // Access Token으로 소셜 Provider에서 사용자 정보 조회
            Map<String, Object> userAttributes = fetchUserAttributes(accessToken, provider);
            // Provider별 응답 형식을 통일된 OAuth2UserInfo로 변환
            return OAuth2UserInfoFactory.getOAuth2UserInfo(provider, userAttributes);
        } catch (Exception e) {
            log.error("소셜 로그인 사용자 정보 조회 실패: provider={}, error={}", provider, e.getMessage());
            throw new BusinessException(MemberErrorCodes.SOCIAL_AUTH_FAILED);
        }
    }

    private Map<String, Object> fetchUserAttributes(String accessToken, String provider) {
        String userInfoUrl = getUserInfoUrl(provider);

        // Bearer Token 인증 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 소셜 Provider API 호출로 사용자 정보 요청
        ResponseEntity<Map> response = restTemplate.exchange(
                userInfoUrl,
                HttpMethod.GET,
                entity,
                Map.class
        );

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new BusinessException(MemberErrorCodes.SOCIAL_AUTH_FAILED);
        }

        return response.getBody();
    }

    private String getUserInfoUrl(String provider) {
        return switch (provider.toLowerCase()) {
            case "google" -> "https://www.googleapis.com/oauth2/v2/userinfo";
            case "kakao" -> "https://kapi.kakao.com/v2/user/me";
            case "naver" -> "https://openapi.naver.com/v1/nid/me";
            default -> throw new IllegalArgumentException("지원하지 않는 OAuth Provider: " + provider);
        };
    }
}