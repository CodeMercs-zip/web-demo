package com.rgs.web_demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "소셜 로그인 요청 DTO")
public class SocialLoginRequestDto {

    @NotBlank(message = "액세스 토큰은 필수입니다")
    @Schema(description = "소셜 Provider Access Token", example = "ya29.a0AfH6...")
    private String accessToken;

    @NotBlank(message = "Provider는 필수입니다")
    @Schema(description = "소셜 Provider", example = "google", allowableValues = {"google", "kakao", "naver"})
    private String provider;
}