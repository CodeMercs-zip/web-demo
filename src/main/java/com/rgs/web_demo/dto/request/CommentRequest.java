package com.rgs.web_demo.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {
	private Long postId;
	private Long parentId; // 대댓글이면 설정
	private String content;
	private String authorEmail;
}
