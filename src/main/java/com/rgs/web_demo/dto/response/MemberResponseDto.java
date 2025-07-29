package com.rgs.web_demo.dto.response;

import java.time.LocalDateTime;

import com.rgs.web_demo.domain.Member;
import com.rgs.web_demo.enumeration.MemberType;
import com.rgs.web_demo.vo.MemberVo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "회원 응답 DTO")
public class MemberResponseDto {

    @Schema(description = "회원 ID", example = "1")
    private Long id;

    @Schema(description = "회원 이름", example = "김개발")
    private String name;

    @Schema(description = "연락처", example = "010-1234-5678")
    private String phoneNumber;

    @Schema(description = "이메일", example = "kim@example.com")
    private String email;

    @Schema(description = "회원 유형", example = "USER")
    private MemberType memberType;

    @Schema(description = "생성 시간", example = "2024-01-01T00:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시간", example = "2024-01-01T00:00:00")
    private LocalDateTime updatedAt;

    public static MemberResponseDto from(Member member) {
        return MemberResponseDto.builder()
                .id(member.getId())
                .name(member.getName())
                .phoneNumber(member.getPhoneNumber())
                .email(member.getEmail())
                .memberType(member.getMemberType())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
    
    public static MemberResponseDto from(MemberVo vo) {
        return MemberResponseDto.builder()
                .name(vo.getName())
                .email(vo.getEmail())
                .phoneNumber(vo.getPhoneNumber())
                .memberType(vo.getMemberType())
                .build();
    }

}