package com.rgs.web_demo.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum PostErrorCodes implements ErrorCode {

    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다.");

    private final HttpStatus status;

    @Getter
    private final String message;

    PostErrorCodes(HttpStatus status, String message) {
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