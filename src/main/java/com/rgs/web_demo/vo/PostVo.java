package com.rgs.web_demo.vo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostVo {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
}
