package com.rgs.web_demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * [comment-1]
 * 개인적인 의견으로 불편하면 지금처럼 사용해도 됨!!
 * RequestDto 는 사용자 요청 데이터를 담는 불변 객체로 설계해서 관리하면 좋을 것 같구 ( 회사에서 밀고 있는 코드 컨밴션인데 좋아보여서 나도 이런식으로 짜려고 의식하겠슴 )
 * 그리고 toEntity 같은 경우에는 RequestDto 에 선언하면 Entity - Dto 의존성이 결합되니까 지양하는 패턴이야
 * 따라서 RequestDto는 아래처럼 설정하고 toEntity 와 같은 기능은 서비스단에서 관리하는게 좋아보여
 */
@Schema(description = "게시글 생성 요청 DTO")
public record PostCreateRequest(
        @Schema(description = "게시글 제목", example = "게시판 기능 개발 중입니다.")
        String title,

        @Schema(description = "게시글 본문", example = "현재 게시판 API와 DB를 설계 중입니다.")
        String content,

        @Schema(description = "작성자 이메일", example = "beargom@example.com")
        String authorEmail,

        @Schema(description = "게시글 타입", example = "QNA / FAQ / GENERAL")
        String postType,

        @Schema(description = "비밀글 여부", example = "false")
        Boolean isSecret
) {}

//@Getter
//@Setter
//@Schema(description = "게시글 생성 요청 DTO")
//public class PostCreateRequest {
//
//    @Schema(description = "게시글 제목", example = "게시판 기능 개발 중입니다.")
//    private String title;
//
//    @Schema(description = "게시글 본문", example = "현재 게시판 API와 DB를 설계 중입니다.")
//    private String content;
//
//    @Schema(description = "작성자 이메일", example = "beargom@example.com")
//    private String authorEmail;
//
//    @Schema(description = "게시글 타입", example = "QNA / FAQ / GENERAL")
//    private String postType;
//
//    @Schema(description = "비밀글 여부", example = "false")
//    private Boolean isSecret;
//
//    public Post toEntity() {
//        return Post.builder()
//                .title(title)
//                .content(content)
//                .authorEmail(authorEmail)
//                .postType(postType)
//                .isSecret(isSecret)
//                .viewCount(0)
//                .build();
//    }
//}


