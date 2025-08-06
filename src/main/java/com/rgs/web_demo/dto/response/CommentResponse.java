package com.rgs.web_demo.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
	private Long id;
	private String content;
	private String authorEmail;
	private LocalDateTime createdAt;
	private List<CommentResponse> children;
}
