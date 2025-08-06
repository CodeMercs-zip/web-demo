package com.rgs.web_demo.dto.request;

import com.rgs.web_demo.domain.Member;
import com.rgs.web_demo.enumeration.MemberType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "회원 생성 요청 DTO")
public class MemberCreateRequestDto {

    @NotBlank(message = "이름은 필수입니다")
    @Schema(description = "회원 이름", example = "김개발")
    private String name;

    @Schema(description = "연락처", example = "010-1234-5678")
    private String phoneNumber;

    @Email(message = "유효한 이메일 형식이어야 합니다")
    @Schema(description = "이메일", example = "kim@example.com")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Schema(description = "비밀번호", example = "password123")
    private String password;

    @NotNull(message = "회원 유형은 필수입니다")
    @Schema(description = "회원 유형", example = "USER")
    private MemberType memberType;

    /**
     * 비밀번호 암호화 및 전화번호 포맷 완료 후 엔티티 변환
     */
    public Member toEntity(String encodedPassword, String formattedPhone) {
        return Member.builder()
                .name(this.name)
                .phoneNumber(formattedPhone)
                .email(this.email)
                .password(encodedPassword)
                .memberType(this.memberType)
                .build();
    }
}
