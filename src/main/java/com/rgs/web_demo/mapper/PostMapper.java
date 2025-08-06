package com.rgs.web_demo.mapper;

import org.springframework.stereotype.Component;

import com.rgs.web_demo.domain.Post;
import com.rgs.web_demo.vo.PostVo;

@Component
public class PostMapper {

    public PostVo toVo(Post post) {
        return PostVo.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .postType(post.getPostType())
            .isSecret(post.isSecret())
            .viewCount(post.getViewCount())
            .createdAt(post.getCreatedAt())
            .authorEmail(post.getAuthorEmail())
            .build();
    }
}
