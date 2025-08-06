package com.rgs.web_demo.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rgs.web_demo.domain.Post;
import com.rgs.web_demo.dto.request.PostCreateRequest;
import com.rgs.web_demo.dto.response.PageResponse;
import com.rgs.web_demo.mapper.PostMapper;
import com.rgs.web_demo.repository.PostRepository;
import com.rgs.web_demo.vo.PostVo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public PageResponse<PostVo> getPosts(int page, int size, String keyword) {
        PageRequest pageable = PageRequest.of(page - 1, size);

        List<Post> posts = postRepository.findByKeyword(keyword, pageable);
        long total = postRepository.countByKeyword(keyword);
        int totalPages = (int) Math.ceil((double) total / size);

        List<PostVo> postVos = posts.stream()
            .map(postMapper::toVo)
            .toList();

        return PageResponse.<PostVo>builder()
            .content(postVos)
            .totalPages(totalPages)
            .totalElements(total)
            .pageNumber(page)
            .build();
    }

    @Transactional
    public void createPost(PostCreateRequest request) {
        Post post = request.toEntity();
        postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new RuntimeException("해당 게시글을 찾을 수 없습니다.");
        }
        postRepository.deleteById(postId);
    }

    public PostVo getPostById(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("해당 게시글을 찾을 수 없습니다."));
        return postMapper.toVo(post);
    }
}
