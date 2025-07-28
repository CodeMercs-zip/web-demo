package com.rgs.web_demo.controller;

import com.rgs.web_demo.domain.Member;
import com.rgs.web_demo.repository.MemberRepository;
import com.rgs.web_demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    @GetMapping("/public")
    public ResponseEntity<Map<String, String>> publicEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "이 엔드포인트는 인증이 필요하지 않습니다.");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/protected")
    public ResponseEntity<Map<String, Object>> protectedEndpoint(
            @RequestHeader("Authorization") String authHeader) {

        try {
            String token = authHeader.replace("Bearer ", "");
            String userId = jwtUtil.getUserIdFromToken(token);

            Optional<Member> member = memberRepository.findById(Long.parseLong(userId));

            Map<String, Object> response = new HashMap<>();
            response.put("message", "토큰 검증 성공!");
            response.put("userId", userId);
            response.put("timestamp", java.time.LocalDateTime.now().toString());

            if (member.isPresent()) {
                Member m = member.get();
                Map<String, Object> memberInfo = new HashMap<>();
                memberInfo.put("id", m.getId());
                memberInfo.put("name", m.getName());
                memberInfo.put("email", m.getEmail());
                memberInfo.put("memberType", m.getMemberType());
                memberInfo.put("provider", m.getProvider());
                memberInfo.put("providerId", m.getProviderId());
                response.put("member", memberInfo);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("토큰 검증 실패: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", "토큰 검증 실패");
            error.put("message", e.getMessage());
            error.put("timestamp", java.time.LocalDateTime.now().toString());
            return ResponseEntity.status(401).body(error);
        }
    }

    @PostMapping("/validate-token")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");

        if (token == null || token.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "토큰이 제공되지 않았습니다.");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            boolean isValid = jwtUtil.validateToken(token);
            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);
            response.put("timestamp", java.time.LocalDateTime.now().toString());

            if (isValid) {
                String userId = jwtUtil.getUserIdFromToken(token);
                response.put("userId", userId);
                
                Optional<Member> member = memberRepository.findById(Long.parseLong(userId));
                if (member.isPresent()) {
                    Member m = member.get();
                    response.put("memberName", m.getName());
                    response.put("memberEmail", m.getEmail());
                }
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("토큰 유효성 검사 실패: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("valid", false);
            error.put("error", e.getMessage());
            error.put("timestamp", java.time.LocalDateTime.now().toString());
            return ResponseEntity.ok(error);
        }
    }

    @GetMapping("/token-info")
    public ResponseEntity<Map<String, Object>> getTokenInfo(
            @RequestHeader("Authorization") String authHeader) {

        try {
            String token = authHeader.replace("Bearer ", "");

            Map<String, Object> response = new HashMap<>();
            response.put("userId", jwtUtil.getUserIdFromToken(token));
            response.put("isValid", jwtUtil.validateToken(token));
            response.put("timestamp", java.time.LocalDateTime.now().toString());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "토큰 정보 조회 실패");
            error.put("message", e.getMessage());
            return ResponseEntity.status(401).body(error);
        }
    }
}