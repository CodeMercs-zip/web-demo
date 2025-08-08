package com.rgs.web_demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InquiryRequestDto(
        // 문의자 이름 (필수, 최대 100자)
        @NotBlank(message = "이름은 필수입니다")
        @Size(max = 100, message = "이름은 100자 이내여야 합니다")
        String fullName,

        // 문의자 이메일 (필수, 이메일 형식, 최대 255자)
        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        @Size(max = 255, message = "이메일은 255자 이내여야 합니다")
        String email,

        // 문의 제목 (필수, 최대 200자)
        @NotBlank(message = "제목은 필수입니다")
        @Size(max = 200, message = "제목은 200자 이내여야 합니다")
        String subject,

        // 문의 내용 (필수, 최대 5000자)
        @NotBlank(message = "메시지는 필수입니다")
        @Size(max = 5000, message = "메시지는 5000자 이내여야 합니다")
        String message,

        // 문의자 전화번호 (선택사항, 최대 20자)
        @Size(max = 20, message = "전화번호는 20자 이내여야 합니다")
        String phoneNumber
) {
}