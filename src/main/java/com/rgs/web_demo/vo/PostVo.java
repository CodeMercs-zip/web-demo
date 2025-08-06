package com.rgs.web_demo.vo;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostVo {
    private Long id;
    private String title;
    private String content;
    private String postType;
    private boolean isSecret;
    private int viewCount;
    private LocalDateTime createdAt;
    private String authorEmail;
}
