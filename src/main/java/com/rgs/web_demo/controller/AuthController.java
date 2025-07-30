package com.rgs.web_demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rgs.web_demo.dto.LoginRequestDto;
import com.rgs.web_demo.dto.LoginResponseDto;
import com.rgs.web_demo.dto.LogoutRequestDto;
import com.rgs.web_demo.dto.request.MemberCreateRequestDto;
import com.rgs.web_demo.dto.response.ApiResponseDto;
import com.rgs.web_demo.dto.response.MemberResponseDto;
import com.rgs.web_demo.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> login(@RequestBody LoginRequestDto request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto<Void>> logout(HttpServletRequest request, @RequestBody LogoutRequestDto logoutRequestDto) {
        return authService.logout(request, logoutRequestDto);
    }


    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto<MemberResponseDto>> signup(@RequestBody MemberCreateRequestDto request) {
        return authService.signup(request);
    }
}
