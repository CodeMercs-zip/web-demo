package com.rgs.web_demo.handler;

import com.rgs.web_demo.domain.Member;
import com.rgs.web_demo.repository.MemberRepository;
import com.rgs.web_demo.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = extractEmail(oauth2User);

        if (email != null) {
            Optional<Member> member = memberRepository.findByEmail(email);
            if (member.isPresent()) {
                Member m = member.get();
                String accessToken = jwtUtil.generateAccessToken(m.getId().toString());
                String refreshToken = jwtUtil.generateRefreshToken(m.getId().toString());

                // 사용자 정보 로깅
                log.info("OAuth2 로그인 성공 - 사용자 정보: ID={}, 이름={}, 이메일={}, 제공자={}, Provider ID={}",
                        m.getId(), m.getName(), m.getEmail(), m.getProvider(), m.getProviderId());

                String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth/callback")
                        .queryParam("accessToken", accessToken)
                        .queryParam("refreshToken", refreshToken)
                        .build().toUriString();

                log.info("OAuth2 로그인 성공: {} -> {}", email, targetUrl);
                getRedirectStrategy().sendRedirect(request, response, targetUrl);
                return;
            }
        }

        // 로그인 실패 시 에러 페이지로 리다이렉트
        String errorUrl = "http://localhost:3000/login?error=oauth2_failed";
        log.error("OAuth2 로그인 실패: 사용자 정보를 찾을 수 없음 (email: {})", email);
        getRedirectStrategy().sendRedirect(request, response, errorUrl);
    }

    private String extractEmail(OAuth2User oauth2User) {
        // Google - 직접 email 속성 확인
        String email = oauth2User.getAttribute("email");
        if (email != null) {
            return email;
        }

        // Kakao - kakao_account.email 확인
        Object kakaoAccount = oauth2User.getAttribute("kakao_account");
        if (kakaoAccount instanceof java.util.Map<?, ?> kakaoMap) {
            email = (String) kakaoMap.get("email");
            if (email != null) {
                return email;
            }
        }

        // Naver - response.email 확인
        Object naverResponse = oauth2User.getAttribute("response");
        if (naverResponse instanceof java.util.Map<?, ?> naverMap) {
            return (String) naverMap.get("email");
        }

        return null;
    }
}