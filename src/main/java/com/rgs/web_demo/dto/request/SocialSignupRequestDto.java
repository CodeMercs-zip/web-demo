package com.rgs.web_demo.dto.request;

import com.rgs.web_demo.enumeration.MemberType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "소셜 회원가입 요청 DTO")
public class SocialSignupRequestDto {

    @NotBlank(message = "액세스 토큰은 필수입니다")
    @Schema(description = "소셜 Provider Access Token", example = "ya29.a0AfH6...")
    private String accessToken;

    @NotBlank(message = "Provider는 필수입니다")
    @Schema(description = "소셜 Provider", example = "google", allowableValues = {"google", "kakao", "naver"})
    private String provider;

    @NotNull(message = "회원 유형은 필수입니다")
    @Schema(description = "회원 유형", example = "USER")
    private MemberType memberType;

    @Schema(description = "연락처", example = "010-1234-5678")
    private String phoneNumber;
}