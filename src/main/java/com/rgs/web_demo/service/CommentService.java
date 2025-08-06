package com.rgs.web_demo.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rgs.web_demo.domain.Comment;
import com.rgs.web_demo.domain.Post;
import com.rgs.web_demo.dto.request.CommentRequest;
import com.rgs.web_demo.dto.response.CommentResponse;
import com.rgs.web_demo.repository.CommentRepository;
import com.rgs.web_demo.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final PostRepository postRepository;

	public void createComment(CommentRequest request) {
		Post post = postRepository.findById(request.getPostId())
				.orElseThrow(() -> new RuntimeException("Post not found"));

		Comment parent = null;
		if (request.getParentId() != null) {
			parent = commentRepository.findById(request.getParentId())
					.orElseThrow(() -> new RuntimeException("Parent comment not found"));
		}

		Comment comment = Comment.builder().post(post).content(request.getContent())
				.authorEmail(request.getAuthorEmail()).parent(parent).build();

		commentRepository.save(comment);
	}

	public List<CommentResponse> getComments(Long postId) {
		List<Comment> topLevel = commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtAsc(postId);
		return topLevel.stream().map(this::toDtoWithChildren).toList();
	}

	private CommentResponse toDtoWithChildren(Comment comment) {
		List<CommentResponse> children = comment.getChildren().stream()
				.sorted(Comparator.comparing(Comment::getCreatedAt)).map(this::toDtoWithChildren).toList();

		return CommentResponse.builder().id(comment.getId()).content(comment.getContent())
				.authorEmail(comment.getAuthorEmail()).createdAt(comment.getCreatedAt()).children(children).build();
	}

	public void deleteComment(Long id) {
		commentRepository.deleteById(id);
	}
}
