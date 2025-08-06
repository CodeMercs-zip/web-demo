package com.rgs.web_demo.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "author_email")
    private String authorEmail;

    @Column(name = "post_type")
    private String postType;

    @Column(name = "is_secret")
    private boolean isSecret;

    @Column(name = "view_count")
    private int viewCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void  updatePost(String title, String content, String authorEmail, String postType, Boolean isSecret) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        if (authorEmail != null) this.authorEmail = authorEmail;
        if (postType != null) this.postType = postType;
        if (isSecret != null) this.isSecret = isSecret;

        this.updatedAt = LocalDateTime.now();
    }

}
