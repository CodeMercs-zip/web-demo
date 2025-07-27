package com.rgs.web_demo.dto.request;

import com.rgs.web_demo.enumeration.MemberType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "회원 수정 요청 DTO")
public class MemberUpdateRequestDto {

    @Schema(description = "회원 이름", example = "김개발")
    private String name;

    @Schema(description = "연락처", example = "010-1234-5678")
    private String phoneNumber;

    @Email(message = "유효한 이메일 형식이어야 합니다")
    @Schema(description = "이메일", example = "kim@example.com")
    private String email;

    @Schema(description = "회원 유형", example = "USER")
    private MemberType memberType;
}