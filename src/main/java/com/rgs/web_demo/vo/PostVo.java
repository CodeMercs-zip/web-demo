package com.rgs.web_demo.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostVo {
    private Long id;
    private String title;
    private String content;
    private String postType;
    private Boolean isSecret;
    private Long viewCount;
    private LocalDateTime createdAt;
    private String authorEmail;
}
