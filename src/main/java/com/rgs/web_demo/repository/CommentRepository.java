package com.rgs.web_demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rgs.web_demo.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdAndParentIsNullOrderByCreatedAtAsc(Long postId);
    List<Comment> findByParentIdOrderByCreatedAtAsc(Long parentId);
}