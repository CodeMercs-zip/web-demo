package com.rgs.web_demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "게시글 생성 요청 DTO")
public class PostCreateRequest {

    @Schema(description = "게시글 제목", example = "게시판 기능 개발 중입니다.")
    private String title;

    @Schema(description = "게시글 본문", example = "현재 게시판 API와 DB를 설계 중입니다.")
    private String content;

    @Schema(description = "작성자 이메일", example = "beargom@example.com")
    private String authorEmail;

    @Schema(description = "게시글 타입", example = "QNA / FAQ / GENERAL")
    private String postType;

    @Schema(description = "비밀글 여부", example = "false")
    private Boolean isSecret;
}
