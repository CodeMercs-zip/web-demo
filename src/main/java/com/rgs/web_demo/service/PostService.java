package com.rgs.web_demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rgs.web_demo.dto.response.PageResponse;
import com.rgs.web_demo.mapper.PostMapper;
import com.rgs.web_demo.vo.PostVo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostMapper postMapper;

    public PageResponse<PostVo> getPosts(int page, int size, String keyword) {
        int offset = (page - 1) * size;

        List<PostVo> posts = postMapper.selectPostList(keyword, offset, size);
        long total = postMapper.countPostList(keyword);

        int totalPages = (int) Math.ceil((double) total / size);

        return PageResponse.<PostVo>builder()
            .content(posts)
            .totalPages(totalPages)
            .totalElements(total)
            .pageNumber(page)
            .build();
    }
}
