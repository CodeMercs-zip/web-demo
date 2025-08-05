// PostController.java
package com.rgs.web_demo.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rgs.web_demo.dto.request.PostCreateRequest;
import com.rgs.web_demo.dto.response.ApiResponseDto;
import com.rgs.web_demo.dto.response.PageResponse;
import com.rgs.web_demo.service.PostService;
import com.rgs.web_demo.vo.PostVo;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	@GetMapping
	public PageResponse<PostVo> getPosts(@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue = "10") int size,
			@RequestParam(name = "keyword", required = false) String keyword) {
		return postService.getPosts(page, size, keyword);
	}

	@PostMapping
	public ApiResponseDto<Void> createPost(@RequestBody PostCreateRequest request) {
		postService.createPost(request);
		return ApiResponseDto.of("게시글이 성공적으로 등록되었습니다.");
	}

	@DeleteMapping("/{postId}")
	public ApiResponseDto<Void> deletePost(@PathVariable Long postId) {
		postService.deletePost(postId);
		return ApiResponseDto.of("게시글이 성공적으로 삭제되었습니다.");
	}

	@GetMapping("/{postId}")
	public ApiResponseDto<PostVo> getPostById(@PathVariable("postId") Long postId) {
	    PostVo post = postService.getPostById(postId);
	    return ApiResponseDto.of("게시글 상세조회 성공", post);
	}

}
