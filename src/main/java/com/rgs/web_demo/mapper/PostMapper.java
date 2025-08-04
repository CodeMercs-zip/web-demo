package com.rgs.web_demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.rgs.web_demo.dto.PostDto;

@Mapper
public interface PostMapper {

    List<PostDto> selectPostList(@Param("keyword") String keyword,
                                 @Param("offset") int offset,
                                 @Param("size") int size);

    long countPostList(@Param("keyword") String keyword);
}
