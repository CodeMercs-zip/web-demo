package com.rgs.web_demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rgs.web_demo.dto.request.CommentRequest;
import com.rgs.web_demo.dto.response.CommentResponse;
import com.rgs.web_demo.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	@PostMapping
	public ResponseEntity<Void> create(@RequestBody CommentRequest request) {
		commentService.createComment(request);
		return ResponseEntity.ok().build();
	}

	@GetMapping
	public ResponseEntity<List<CommentResponse>> getByPost(@RequestParam Long postId) {
		return ResponseEntity.ok(commentService.getComments(postId));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		commentService.deleteComment(id);
		return ResponseEntity.ok().build();
	}
}
