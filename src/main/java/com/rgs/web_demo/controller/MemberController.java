package com.rgs.web_demo.controller;

import com.rgs.web_demo.dto.request.*;
import com.rgs.web_demo.dto.response.ApiResponseDto;
import com.rgs.web_demo.dto.response.AuthResponseDto;
import com.rgs.web_demo.dto.response.MemberResponseDto;
import com.rgs.web_demo.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원 관리 API")
public class MemberController extends BaseController {

    private final MemberService memberService;

    @PostMapping("/signup")
    @Operation(summary = "일반 회원가입", description = "이메일/패스워드로 회원가입하고 JWT 토큰을 발급받습니다.")
    public ResponseEntity<ApiResponseDto<AuthResponseDto>> signup(
            @Valid @RequestBody MemberCreateRequestDto requestDto) {
        AuthResponseDto authResponse = memberService.signup(requestDto);
        return ok(authResponse);
    }

    @PostMapping("/login")
    @Operation(summary = "일반 로그인", description = "이메일/패스워드로 로그인하고 JWT 토큰을 발급받습니다.")
    public ResponseEntity<ApiResponseDto<AuthResponseDto>> login(
            @Valid @RequestBody LoginRequestDto requestDto) {
        AuthResponseDto authResponse = memberService.login(requestDto);
        return ok(authResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "회원 단일 조회", description = "ID로 회원 정보를 조회합니다.")
    public ResponseEntity<ApiResponseDto<MemberResponseDto>> getMember(
            @Parameter(description = "회원 ID", required = true)
            @PathVariable Long id) {
        MemberResponseDto member = memberService.getMember(id);
        return ok(member);
    }

    @GetMapping
    @Operation(summary = "회원 목록 조회", description = "회원 목록을 페이징으로 조회합니다.")
    public ResponseEntity<ApiResponseDto<Page<MemberResponseDto>>> getMembers(
            @Parameter(description = "페이징 정보")
            @PageableDefault(size = 20) Pageable pageable) {
        Page<MemberResponseDto> members = memberService.getMembers(pageable);
        return ok(members);
    }

    @PutMapping("/{id}")
    @Operation(summary = "회원 수정", description = "회원 정보를 수정합니다.")
    public ResponseEntity<ApiResponseDto<MemberResponseDto>> updateMember(
            @Parameter(description = "회원 ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody MemberUpdateRequestDto requestDto) {
        MemberResponseDto member = memberService.updateMember(id, requestDto);
        return ok(member);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "회원 삭제", description = "회원을 삭제합니다.")
    public ResponseEntity<ApiResponseDto<Void>> deleteMember(
            @Parameter(description = "회원 ID", required = true)
            @PathVariable Long id) {
        memberService.deleteMember(id);
        return ok(null);
    }

    @PostMapping("/social/login")
    @Operation(summary = "소셜 로그인", description = "소셜 Provider Access Token으로 로그인합니다.")
    public ResponseEntity<ApiResponseDto<AuthResponseDto>> socialLogin(
            @Valid @RequestBody SocialLoginRequestDto requestDto) {
        AuthResponseDto authResponse = memberService.socialLogin(requestDto);
        return ok(authResponse);
    }

    @PostMapping("/social/signup")
    @Operation(summary = "소셜 회원가입", description = "소셜 Provider Access Token으로 회원가입합니다.")
    public ResponseEntity<ApiResponseDto<AuthResponseDto>> socialSignup(
            @Valid @RequestBody SocialSignupRequestDto requestDto) {
        AuthResponseDto authResponse = memberService.socialSignup(requestDto);
        return ok(authResponse);
    }
}