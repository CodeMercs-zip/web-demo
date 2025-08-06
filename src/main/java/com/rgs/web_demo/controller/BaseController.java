package com.rgs.web_demo.controller;

import com.rgs.web_demo.dto.response.ApiResponseDto;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {

    protected <T> ResponseEntity<ApiResponseDto<T>> ok(T data) {
        return ResponseEntity.ok(ApiResponseDto.of(data));
    }
}