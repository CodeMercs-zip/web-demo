package com.rgs.web_demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum MemberErrorCodes implements ErrorCode {

    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    ALREADY_EXISTS_SOCIAL_MEMBER(HttpStatus.CONFLICT, "이미 가입된 소셜 회원입니다."),
    ALREADY_EXISTS_EMAIL(HttpStatus.CONFLICT, "해당 이메일로 이미 가입된 회원이 있습니다."),
    NOT_FOUND_SOCIAL_MEMBER(HttpStatus.NOT_FOUND, "소셜 회원 정보가 없습니다. 회원가입을 먼저 진행해주세요."),
    SOCIAL_AUTH_FAILED(HttpStatus.BAD_REQUEST, "소셜 인증에 실패했습니다."),
    INVALID_LOGIN_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 잘못되었습니다.");

    private final HttpStatus status;

    @Getter
    private final String message;

    MemberErrorCodes(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return status;
    }

    public String getCode() {
        return this.name();
    }
}