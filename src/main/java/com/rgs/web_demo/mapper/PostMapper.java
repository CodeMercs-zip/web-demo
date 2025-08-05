package com.rgs.web_demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.rgs.web_demo.dto.request.PostCreateRequest;
import com.rgs.web_demo.vo.PostVo;

@Mapper
public interface PostMapper {

    List<PostVo> selectPostList(@Param("keyword") String keyword,
                                 @Param("offset") int offset,
                                 @Param("size") int size);

    long countPostList(@Param("keyword") String keyword);

    void insertPost(PostCreateRequest request);

    void deletePostById(@Param("postId") Long postId);

    PostVo findById(Long postId);

}
