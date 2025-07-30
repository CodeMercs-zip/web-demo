package com.rgs.web_demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API 응답 DTO")
public class ApiResponseDto<T> {

    @Schema(description = "응답 상태/코드", example = "SUCCESS")
    private String message;

    @Schema(description = "응답 데이터 또는 에러 메시지")
    private T data;

    public static <T> ApiResponseDto<T> of(String message, T data) {
        return new ApiResponseDto<>(message, data);
    }

    public static <T> ApiResponseDto<T> of(T data) {
        return new ApiResponseDto<>("요청이 성공적으로 처리되었습니다.", data);
    }

    public static <T> ApiResponseDto<T> of(String message) {
        return new ApiResponseDto<>(message, null);
    }
}